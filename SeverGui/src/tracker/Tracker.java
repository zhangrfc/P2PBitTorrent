package tracker;


import java.net.InetAddress;
import java.net.UnknownHostException;

import JvodInfrastructure.PackageServers.PackageServer;
import JvoidInfrastructure.*;
import JvodInfrastructure.Datas.*;

public class Tracker {
	public static void main(String[] args) throws UnknownHostException {
		PackageServer ps = new PackageServer(7789, new TrackerHandler());
		System.out.println( InetAddress.getLocalHost().getHostAddress());
    	ps.run();
	}

}
