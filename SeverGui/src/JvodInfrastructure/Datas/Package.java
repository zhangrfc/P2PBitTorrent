package JvodInfrastructure.Datas;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Package implements Serializable{

	private static final long serialVersionUID = -530208061360449588L;
	private Map<String, String> properities = new HashMap<String, String>();
	private byte[] data;
	private Torrent torrentT;
	private ArrayList<Torrent> Tlist;
	private ArrayList<User> Ulist;
	
	/**
     * Set data inside a package.
     *
     * @param input data
     */
	public Package(byte[] inData){
		data = inData;
	}
	
	/**
     * Set Ulist of users inside a package.
     *
     * @param input array of users
     */
	public void setUList(ArrayList<User> input){
		Ulist = input;
	}
	
	/**
     * Get list of users inside a package.
     *
     * @return the user list want to get
     */
	public ArrayList<User> getUList(){
		return Ulist;
	}

	/**
     * Set torrent inside a package.
     *
     * @param input torrent to set
     */
	public void setT(Torrent input){
		torrentT = input;
	}
	
	/**
     * Get torrent inside a package.
     *
     * @return the torrent want to get
     */
	public Torrent getT(){
		return torrentT;
	}

	/**
     * Set TList of torrent inside a package.
     *
     * @param input array of torrent
     */
	public void setTList(ArrayList<Torrent> input){
		Tlist = input;
	}
	
	/**
     * Get list of torrent inside a package.
     *
     * @return the torrent list want to get
     */
	public ArrayList<Torrent> getTList(){
		return Tlist;
	}

	/**
     * Set properities inside a package.
     *
     * @param input properities to set
     */
	public void setP(String p, String v){
		properities.put(p, v);
	}

	/**
     * Get properities inside a package.
     *
     * @return the properities want to get
     */	
	public String getP(String p){
		return properities.get(p);
	}
	
	/**
     * Set data inside a package.
     *
     * @param input data to set
     */
	public void setD(byte[] inData){
		data = inData;
	}
	
	/**
     * Get data inside a package.
     *
     * @return the data want to get
     */
	public byte[] getD(){
		return data;
	}

	/**
     * Serialize a input package
     *
     * @param input package to serialize
     *
     * @return serialzed bytes of input package p
     */
	static public byte[] serialize(Package p){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] bytes = null;
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(p);
		  bytes = bos.toByteArray();
		} catch(Exception e){
			System.out.println("Exception should be handled");
		} finally {
		  try {
		    if (out != null) {
		      out.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		  try {
		    bos.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		return bytes;
	}

	/**
     * Deserialize input bytes
     *
     * @param input bytes to deserialize
     *
     * @return deserialzed package of input bytes
     */
	static public Package deserialize(byte[] bytes){
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		Package p = null;
		try {
		  in = new ObjectInputStream(bis);
		  p = (Package)in.readObject(); 
		} catch(Exception e) {
			System.out.println("Exception should be handled");
		}
		finally {
		  try {
		    bis.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		  try {
		    if (in != null) {
		      in.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}
		return p;
	}
}
