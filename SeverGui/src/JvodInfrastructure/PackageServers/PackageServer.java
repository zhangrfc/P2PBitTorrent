package JvodInfrastructure.PackageServers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import JvodInfrastructure.Handlers.PackageHandler;
import JvodInfrastructure.Datas.Package;

public class PackageServer {
    private ServerSocket serverSocket = null;
    private int port;
    private AtomicBoolean done;
    private PackageHandler ph;
	
	/**
     * Ctor for a new PackageServer
     *
     * @param input port
     * @param input handler to deal with a package
     */    
    public PackageServer(int port, PackageHandler ph){
    	this.port = port;
    	this.done = new AtomicBoolean();
    	this.done.set(false);
    	this.ph = ph;
    }
    
    /**
     * Craete a new thread with a new socketAcceptor and run
	 *
     */    
    public void run(){
    	SocketAcceptor sl = new SocketAcceptor();
    	Thread t = new Thread(sl);
		t.start();
    }
    
    /**
     * Set a boolean variable indicating the completion
	 *
     */
    void done(){
    	this.done.set(true);
    }
    
    /**
     * init server socket, accept new connection and get listener thread
     */
    class SocketAcceptor implements Runnable{
		@Override
		public void run() {
		    try {
		        serverSocket = new ServerSocket(port);
		    } catch (IOException ex) {
		        System.out.println("Can't setup server on this port number. ");
		        return;
		    }
		    while(!done.get()){
			    Socket socket = null;
			    try {
			        socket = serverSocket.accept();
			    } catch (IOException ex) {
			        System.out.println("Can't accept client connection. ");
			        continue;
			    }
			    SocketListener sl = new SocketListener(socket);
		    	Thread t = new Thread(sl);
				t.start();
		    }
		    try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    class SocketListener implements Runnable{
    	private Socket socket;
    	
		/**
		* Ctor for a new SocketListener
		*
		* @param input socket
		*/  
    	SocketListener(Socket socket){
    		this.socket = socket;
    	}

    	/**
		* A new thread that listens and managing the logic of recieving and writing a new file
		*
		*/  
		@Override
		public void run() {
			if(socket == null){
				return;
			} else {
				try{
				    DataInputStream in = new DataInputStream(socket.getInputStream());
				    int pSize = in.readInt();
				    byte[] packageBytes = new byte[pSize];
				    System.out.println("pSize" + pSize);

				    System.out.println("begin read");
				    in.readFully(packageBytes);
				    Package p = Package.deserialize(packageBytes);

				    Package res = ph.handle(p);

					byte[] bytes = Package.serialize(res);
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeInt(bytes.length);
					System.out.println("write length "+ bytes.length);
					out.write(bytes);
					out.flush();
					out.close();
					in.close();
					socket.close();
				} catch (Exception e){
					e.printStackTrace();
					System.out.println("Package server EXCEPTION");
				} finally {
				}
			}
			
		}
    }
    
//    public static void main(String[] args) {
//    	PackageServer ps = new PackageServer(6666, new PackageHandler());
//    	ps.run();
//	}
}
