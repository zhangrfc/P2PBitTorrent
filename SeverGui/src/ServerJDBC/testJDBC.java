package ServerJDBC;

import java.util.ArrayList;
import java.util.List;

import JvodInfrastructure.Datas.User;



public class testJDBC
{
  public static void main(String[] argc)
  {
    Database db = new Database();
    try
    {
      db.connect();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    
    db.droptable();
    db.createtable();
    
    /*add peers*/
    db.addpeerinfo("127.0.0.1", "1024", "book", 10, "hash");
    db.addpeerinfo("127.0.0.2", "1024", "book", 10, "hash");
    db.addpeerinfo("127.0.0.3", "1024", "book", 10, "hash");
    db.addpeerinfo("127.0.0.4", "1024", "book", 10, "hash");
    db.addpeerinfo("127.0.0.1", "1023", "book", 10, "hash");
    db.addpeerinfo("127.0.0.2", "1025", "book", 10, "hash");
    db.addpeerinfo("127.0.0.6", "1024", "book", 10, "hash");
    db.addpeerinfo("127.0.0.2", "1024", "book0", 10, "hash");
    db.addpeerinfo("127.0.0.2", "1024", "book1", 10, "hash");
    db.addpeerinfo("127.0.0.2", "1024", "book2", 10, "hash");
    db.addpeerinfo("127.0.0.2", "1024", "book3", 10, "hash");
    /*delete peers*/
    db.deletepeerinfo("127.0.0.2", "1024");
    /*get peers*/
    ArrayList<User>user = new ArrayList<User>();
    db.getpeer(user, "book", 10, "hash");
    //System.out.println(user);
    /*add torrents*/
    db.addtorrent("book0", 10, "hash");
    db.addtorrent("book1", 10, "hash");
    db.addtorrent("book3", 10, "hash");
    db.addtorrent("book4", 10, "hash");
    /*delete torrents*/
    db.deletetorrent("book0", 10, "hash");
    /*verify torrents*/
    System.out.println(db.verifytorrent("book1", 10, "hash"));
    System.out.println(db.verifytorrent("book3", 10, "hash"));
    System.out.println(db.verifytorrent("book4", 10, "hash"));
    System.out.println(db.verifytorrent("book2", 10, "hash"));
    System.out.println(db.verifytorrent("book1", 11, "hash"));
    System.out.println(db.verifytorrent("book1", 10, "hash1"));
    db.disconnect();
  }
}
