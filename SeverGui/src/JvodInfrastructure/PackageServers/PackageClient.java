package JvodInfrastructure.PackageServers;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import JvodInfrastructure.Handlers.ResponseHandler;
import JvodInfrastructure.Datas.Package;

public class PackageClient {
	private String host;
	private int port;
	private ResponseHandler rh;

	/**
     * Ctor for PackageClient
     *
     * @param input host
     * @param input port
     * @param input ResponseHandler
     */
	public PackageClient(String host, int port, ResponseHandler rh){
		this.host = host;
		this.port = port;
		this.rh = rh;
	}
	

	/**
     * Send a new package over the socket to the remote caller
     *
     * @param input package
     */
	public void sendPackage(Package p) throws Exception{
		System.out.println("Sending package to " + host + " " + port);
		byte[] bytes = Package.serialize(p);
		Package res = null;
		try {
			Socket socket = new Socket(host, port);
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());
			out.writeInt(bytes.length);
			System.out.println("write length "+ bytes.length);
			out.write(bytes);
			out.flush();
			
		    DataInputStream in = new DataInputStream(socket.getInputStream());
		    int pSize = in.readInt();
//			System.out.println("Check Point");

		    byte[] packageBytes = new byte[pSize];
		    in.readFully(packageBytes);
		    res = Package.deserialize(packageBytes);
		    // get other 
			out.close();
		    in.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("Req-Res error");
		}
		rh.handle(res);
		
	}
//	public static void main(String[] args) throws Exception {
//		PackageClient pc = new PackageClient("127.0.0.1", 6666, new ResponseHandler());
//		Package p = new Package(null);
//		p.setP("hello", "world");
//		pc.sendPackage(p);
//	}
}
