package JvoidInfrastructure;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import JvodInfrastructure.Handlers.ResponseHandler;
import JvodInfrastructure.PackageServers.PackageClient;



public class main {
	public static void main(String[] args) throws IOException {
		String filename = "Lec02_ObjectsTypesStyle_2per.pdf";
		ResponseHandler newRH = new ResponseHandler(constData.filePath, 7779);
		PackageClient pc = new PackageClient("35.2.83.42", 7789, newRH);

//		Package P = newRH.register(filename);
//		Package P = newRH.startProgram();
//		pc.sendPackage(P);
	}
	

}
