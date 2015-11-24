package tracker;

import java.util.ArrayList;

import JvodInfrastructure.Datas.*;
import JvodInfrastructure.Datas.Package;
import JvodInfrastructure.Handlers.PackageHandler;
import JvoidInfrastructure.*;
import ServerJDBC.Database;


public class TrackerHandler extends PackageHandler {

	public Package res;
	public Package req;
	private Database db;

	/**
     * Ctor for TrackerHandler with empty attributes
     *
     */
	TrackerHandler(){
		res = new Package(null);
		db = new Database();
	  try{
      db.connect();
    }
    catch(Exception e){
      
    }
	  //db.disconnect();
	}

	/**
     * Route the input package to correspoding handler
     *
     * @param input package
     *
     * @return output package
     */
	public synchronized Package handle(Package p){
		
		System.out.println("get package");
		String op = p.getP("op");
		req = p;
	
		if (op.equals("registerTorrent")){
			register();
		}
		if (op.equals("start")){
			startClient();
					
		}
		if (op.equals("shutDown")){
			shutDown();
		}
		if (op.equals("getPeer")){
			getPeer();
		}
		res.setP("op", op);
		System.out.println(op);
		System.out.println(res.getP("res_message"));
		
		return res;
	}

	/**
     * Get available peers for clients to connect
     *
     */
	private void getPeer() {
		// TODO Auto-generated method stub
		Torrent rt = req.getT();
		res.setT(rt);
		String fileName = rt.fileName;
		int size = rt.size;
		String hashData = rt.hashData;
		// get peer
		ArrayList<User> peerList = new ArrayList<User>();
		db.getpeer(peerList, fileName, size, hashData);
		System.out.println("get peer in tracker");
		for (User U : peerList){
		  System.out.println(U.ip + "   "  + U.port);
		}
		res.setUList(peerList);
		res.setP("res_message", "success");
		
	}

	/**
     * Remove the shutting down client from available peer list
     *
     */
	private void shutDown() {
		// TODO Auto-generated method stub
		//delete user
		String userIp = req.getP("ip");
		String userPort = req.getP("port");
		db.deletepeerinfo(userIp, userPort);
		res.setP("res_message", "success");
	}

	/**
     * Regisiter the client with all its file when its starting
     *
     */
	private void startClient() {
		// TODO Auto-generated method stub
		ArrayList<Torrent> tlist = req.getTList();
		String userIp = req.getP("ip");
		String userPort = req.getP("port");
		// for loop add user
		for (Torrent rt : tlist){
			String fileName = rt.fileName;
			int size = rt.size;
			String hashData = rt.hashData;
			db.addtorrent(fileName, size, hashData);
			db.addpeerinfo(userIp, userPort, fileName, size, hashData);
		}
		res.setP("res_message", "success");
	}
	
	/**
     * Regisiter a new torrent
     *
     */
	private void register() {
		// TODO Auto-generated method stub
		Torrent rt = req.getT();
		String fileName = rt.fileName;
		int size = rt.size;
		String hashData = rt.hashData;
		String userIp = req.getP("ip");
		String userPort = req.getP("port");
		
		System.out.println("test add torretn :" + userIp);
		// add torrent 
		db.addtorrent(fileName, size, hashData);
		// add user
		db.addpeerinfo(userIp, userPort, fileName, size, hashData);
		res.setP("res_message", "success");
	}

}
