package JvodClient;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import JvodInfrastructure.Datas.Torrent;
import JvodInfrastructure.Datas.User;
import JvodInfrastructure.Datas.Package;
import JvodInfrastructure.FileServers.FileWriter;
import JvodInfrastructure.PackageServers.PackageClient;

public class JvodClientFileDownloader {

	
	/**
     * Download a file from an array of users for torrent t
     * Writes to a given path
     *
	 * @param input torrent
     * @param input array of avaiable peers to connect
     * @param input path to download
     */
	public boolean download(Torrent t, List<User> users, String writePath){
		
		FileWriter fw = new FileWriter(t.fileName, t.size, writePath);
		List<Worker> workers = new ArrayList<Worker>();
		System.out.println("Find " + users.size() + " Peers");
		for(User u : users){
			Worker w = new Worker(u, fw);
			workers.add(w);
			w.start();
		}
		
		for(Worker w : workers){
			try {
				w.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.err.println("Joint fail");
				e.printStackTrace();
			}
		}
		System.out.println("Download finish");
		return fw.successful();
	}

	private class Worker extends Thread{
		FileWriter fw;
		User user;

		/**
		* Ctor for worker
		*
		* @param input user
		* @param input filewriter
		*/
		Worker(User u, FileWriter fw){
			this.fw = fw;
			this.user = u;
		}
		@Override
		public void run(){
			int errC = 0;
			System.out.println("Run work thread for " + user.ip + " "+ user.port );
			PackageClient pc = null;
		      try
		      {
		        pc = new PackageClient(user.ip, user.port, fw.getHandler());
		      }
		      catch( UnknownHostException e1 )
		      {
		        // TODO Auto-generated catch block
		        e1.printStackTrace();
		      }
				Package p = fw.getRequest();
				while(p != null){
					try {
						pc.sendPackage(p);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						errC ++;
						if(errC > 3){
							System.out.println("Server should be down, Work thread die");
							return;
						}
						e.printStackTrace();
					}
					p = fw.getRequest();
				}
			}
		
	}
	
	public static void main(String args[]) {
		
		// do unit test multiple servers
		Torrent t = null;
		try {
			t = new Torrent("v.mp4", 106694563);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String writePath = "/Users/wjkcow/Desktop/v1.mp4";
		List<User> users = new ArrayList<User>();
//		for(int port = 6000; port < 6001; ++port){
//			User u = new User(port, "127.0.0.1");
//			users.add(u);
//		}
		User u = new User(6000, "35.2.90.166");
		users.add(u);
		u = new User(6000, "35.2.100.234");
		users.add(u);
		u = new User(6000, "127.0.0.1");
		users.add(u);
		JvodClientFileDownloader jcfd = new JvodClientFileDownloader();
		if(!jcfd.download(t, users, writePath)){
			System.err.println("Download fail");
		}
	}
}
