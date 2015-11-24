package JvodInfrastructure.FileServers;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import JvodInfrastructure.Datas.Package;
import JvodInfrastructure.Handlers.ResponseHandler;

public class FileWriter {

	private static int SEG_LEN = 1024*1024;
	private String filename;
	private AtomicInteger size = new AtomicInteger();
	private String path;
	private ConcurrentLinkedQueue<Package> requests = new ConcurrentLinkedQueue<Package>();
	private ConcurrentMap<Integer, Package> unACKs = new ConcurrentHashMap<Integer, Package>();
	private DiskWriter writerThread;
	private boolean successful = false;

	/**
	* Ctor for FileWriter
	*
	* @param input filename to write
	* @param input size of the file
	* @param input path of the file
	*/
	public FileWriter(String filename, int size, String path){
		this.filename = filename;
		this.size.set(size);
		//this.hash = hash;
		this.path = path;
		writerThread = new DiskWriter(path, size);
		writerThread.start();
		requests.addAll(requestFactory(filename, size, path));
	}
	
	/**
	* Kill and join a thread
	*
	* @return a boolean indicate a success of join and kill
	*/
	public boolean successful(){
		writerThread.kill();
		try {
			writerThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return successful;
	}

	/**
	* Build a request based on input file attributes
	*
	* @param input filename 
	* @param input size of the file
	* @param input path of the file
	*
	* @return the list of package after execution
	*/
	static private List<Package> requestFactory(String filename, int size, String path){
		Integer offset = 0;
		Integer id = 0;
		List<Package> reqs = new ArrayList<Package>();
		System.out.println("Initing requests for file " + filename);
		while( offset < size){
			Integer seg_length = SEG_LEN;
			if(seg_length > size - offset){
				seg_length = size - offset;
			}
			Package req = new Package(null);
			req.setP("package_type", "data_request");
			req.setP("filename", filename);
			req.setP("seg_offset", offset.toString());
			req.setP("seg_length", seg_length.toString());
			req.setP("req_ID", id.toString());
			offset = offset + seg_length;
			id++;
			reqs.add(req);
		}
		System.out.println("Total number of requests needed " + reqs.size());
		return reqs;
	}
	
	/**
	* Get the request for the next part of file
	* This method is thread safe 
	* Returns null if no more partition is left for a file
	*
	* @return the request package
	*/
	public Package getRequest(){
		Package p = requests.poll();
		if(p != null){
			Integer id = Integer.parseInt(p.getP("req_ID"));
			System.out.println("New request polled " + p.getP("filename")
				+ " offset "+ p.getP("seg_offset") + " length " + p.getP("seg_length"));
			System.out.println("Request ID" + id + "Request remain " + requests.size());
			unACKs.putIfAbsent(id, p);
		} else {
			ConcurrentLinkedQueue<Package> uack_rs = new ConcurrentLinkedQueue<Package>(unACKs.values());
			p = uack_rs.poll();
		}
		return p;
	}
	
	/**
	* Get a new ResponseHandler
	*
	* @return the new ResponseHandler
	*/
	public ResponseHandler getHandler() throws UnknownHostException{		
		return new FileResponseHandler();
	}
	
	private class DiskWriter extends Thread{
		private ConcurrentLinkedQueue<Package> responses = new ConcurrentLinkedQueue<Package>();
		private AtomicInteger receivedSize = new AtomicInteger();
		private AtomicBoolean done;
		byte[] buffer;
		String path;
		
		/**
		* Ctor for DiskWriter
		*
		* @param input path of the file
		* @param input size of the file
		*/
		DiskWriter(String path, int size){
			this.buffer = new byte[size];
			this.path = path;
			this.receivedSize.set(0);
			this.done = new AtomicBoolean();
			this.done.set(false);
		}

		/**
		* Call writeBuffer() to run
		*/
		@Override
		public void run() {
			writeBuffer();
			if(unACKs.isEmpty() && requests.isEmpty()){
				successful = true;
				flush();
			} else {
				System.err.println("Rquests size" + requests.size() + " unAck size" + unACKs.size());
			}
			System.out.println("Disk Writer dies");
		}		
		public synchronized void kill(){
			System.out.println("killing");
			if(receivedSize.get() < size.get()){
				done.set(true);
			}
			notify();
		}

		/**
		* Add a new package
		*
		* @param input package 
		*
		*/
		public synchronized void newPackage(Package p){
			System.out.println(p);
			System.out.println("Responses size " + responses.size());
			responses.add(p);
			notify();
		}

		/**
		* Call writeBuffer() to run
		*/
		private synchronized void writeBuffer(){
			while(receivedSize.get() < size.get() && !done.get()){
				while(responses.isEmpty() && !done.get()){
					try {
						System.out.println("Writer buffer going to sleep " + done.get());
						wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				Package p = responses.poll();
				Integer id = Integer.parseInt(p.getP("req_ID"));
				if(p != null && unACKs.containsKey(id)){
					write(p);			
					System.out.println("Writer Thread working " + receivedSize + "," + size);
					System.out.println("Response remain " + responses.size());
				}
				unACKs.remove(id);

				System.out.println("Buffer write done, isFinish " + receivedSize.equals(size));

			}

		}
		

		/**
		* write a parition into the file buffer
		* the package is with type of data_response and file_witer_id=1
		* this method is thread safe
		* 
		* @param input package 
		*
		*/
		private void write(Package p){
		     int length = Integer.parseInt(p.getP("seg_length"));
		     int offset = Integer.parseInt(p.getP("seg_offset"));
		     receivedSize.addAndGet(length);
		     System.arraycopy(p.getD(), 0, buffer, offset, length);
		}

		/**
		* Flush the buffer of file to disk
		*/
		private void flush(){
			System.out.println("going to flush file " + path);
			FileOutputStream fos;
			BufferedOutputStream bos;
			try {
				fos = new FileOutputStream(path);
				bos = new BufferedOutputStream(fos);
				bos.write(buffer);
				bos.flush();
				buffer = null;
				bos.close();
				fos.close();
				System.out.println("File write sucessfully " + path);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	
	class FileResponseHandler extends ResponseHandler{
		public FileResponseHandler() throws UnknownHostException
    {
      super();
    }

	/**
	* Call a writer thread with input package 
	*
	* @param input package 
	*
	*/
    public void handle(Package p) throws Exception{
			if(p != null){
				System.out.println("Response received " + p.getP("filename")
						+ " offset "+ p.getP("seg_offset") + " length " + p.getP("seg_length"));

				writerThread.newPackage(p);
			} else {
				throw new Exception();
			}
		}
	}
	
	static class UnitTest{
		static FileWriter fw;
		static FileServer fs;
		static void randomLag(){
			Random generator = new Random();
			int i = generator.nextInt(1000);
            try {
				Thread.sleep(i);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		static private class Client extends Thread{

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int errC = 0;
				Package req;
				while((req=fw.getRequest()) != null){
					randomLag();
					try {
						fw.getHandler().handle(fs.handle(req));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						errC++;
						e.printStackTrace();
						if(errC > 3){
							return;
						}
					}
				}
			}
			
		}
		
		
		
		static void run(){
		    File file = new File("/Users/wjkcow/Desktop/v.mp4");
		    long length = file.length();
		    if (length > Integer.MAX_VALUE) {
		        System.out.println("File is too large.");
		    }
		    System.out.println(length);
			fw = new FileWriter("t.dmg", (int) (length), "/Users/wjkcow/Desktop/t1.dmg");
			fs = new FileServer();
			//fs.newFile("t.dmg", (int) (length), "not used", "/Users/wjkcow/Desktop/t.dmg");
			fs.newFile("t.dmg", "/Users/wjkcow/Desktop/v.mkv");
			Thread t1 = new Client();
			Thread t2 = new Client();
			Thread t3 = new Client();
			Thread t4 = new Client();
			Thread t5 = new Client();
			Thread t6 = new Client();
			t1.start();
//			t2.start();
//			t3.start();
//			t4.start();
//			t5.start();
//			t6.start();
			
		}
		
		
	}
	
	public static void main(String[] args){
		UnitTest.run();
	}
}
