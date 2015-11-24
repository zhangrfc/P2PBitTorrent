package JvodInfrastructure.Datas;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import JvoidInfrastructure.constData;

public class Torrent implements Serializable{
	public String fileName;
	public int size;
	public String hashData;
    /**
     * Ctor to initialize a new torrent
     *
     * @param input filename
     * @param input size
     */
	public Torrent(String _filename, int _size) throws IOException{
		this.fileName = _filename;
		this.size = _size;
		Path path = Paths.get(constData.filePath + _filename);
		byte[] data = Files.readAllBytes(path);
		this.hashData = getMD5(data);
		System.out.println(data.length);
	}

    /**
     * Compute MD5 from input bytes
     *
     * @param input bytes to compute MD%
     *
     * @return the string after MD5 computation
     */
	public static String getMD5(byte[] input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input);
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
