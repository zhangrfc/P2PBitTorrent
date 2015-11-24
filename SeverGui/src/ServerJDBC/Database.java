package ServerJDBC;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import JvodInfrastructure.Datas.User;




public class Database
{
  private Connection con;
  private Statement stmt = null;
  private String sql = "";

  public void connect() throws Exception
  {
    if( con != null )
      return;

    try
    {
      Class.forName("com.mysql.jdbc.Driver");
    }
    catch( ClassNotFoundException e )
    {
      throw new Exception("Driver Not Found");
    }

    String connectionUrl = "jdbc:mysql://localhost:3306/JVODDB";
    String user = "root";
    String password = "group4";

    try
    {
      con = DriverManager.getConnection(connectionUrl, user, password);
    }
    catch( SQLException e )
    {
      System.out.println("Connection Failed!");
    }

    System.out.println("Connected: " + con);
  }

  public void disconnect()
  {
    if( con != null )
    {
      try
      {
        con.close();
      }
      catch( SQLException e )
      {
        System.out.println("Can't close connection");
      }
    }
  }

  public void createtable()
  {
    try
    {
      stmt = con.createStatement();
      sql = "CREATE TABLE PEERINFO " 
          + "(PEERIP CHAR(20) NOT NULL, "
          + "PEERPORT CHAR(10) NOT NULL, "
          + "FILENAME CHAR(20) NOT NULL, " 
          + "SIZE CHAR(10) NOT NULL, "
          + "HASH CHAR(32) NOT NULL, "
          + "CONSTRAINT PEERINFO PRIMARY KEY (PEERIP, PEERPORT, FILENAME))";
      stmt.executeUpdate(sql);
      sql = "CREATE TABLE TORRENTINFO "
          + "(FILENAME CHAR(20) NOT NULL, " 
          + "SIZE CHAR(10) NOT NULL, "
          + "HASH CHAR(32) NOT NULL, "
          + "CONSTRAINT TORRENTINFO PRIMARY KEY (FILENAME, SIZE, HASH))";
      stmt.executeUpdate(sql);
      System.out.println("Success to create table");
      
    }
    catch( SQLException e )
    {
      System.out.println("Fail to create table");
      e.printStackTrace();
    }
  }

  public void droptable()
  {
    try
    {
      stmt = con.createStatement();
      sql = "DROP TABLE PEERINFO";
      stmt.executeUpdate(sql);
      sql = "DROP TABLE TORRENTINFO";
      stmt.executeUpdate(sql);
      System.out.println("Success to drop table");
    }
    catch( SQLException e )
    {
      System.out.println("Fail to drop table");
      e.printStackTrace();
    }
  }

  public void addpeerinfo(String peerip, String peerport, String filename, int size, String hash)
  {
    try
    {
      String sizestr = Integer.toString(size);
      stmt = con.createStatement();
      sql = "INSERT INTO PEERINFO VALUES "
          + "('" + peerip + "','" + peerport + "','"
          + filename + "','" + sizestr + "','" + hash + "')";
      stmt.executeUpdate(sql);
      System.out.println("Success to insert peer info!");
    }
    catch( SQLException e )
    {
      System.out.println("Fail to insert peer info!");
      e.printStackTrace();
    }
  }
  
  public void addtorrent(String filename, int size, String hash)
  {
    try
    {
      String sizestr = Integer.toString(size);
      stmt = con.createStatement();
      sql = "INSERT INTO TORRENTINFO VALUES "
          + "('" + filename + "','" + sizestr + "','" + hash + "')";
      stmt.executeUpdate(sql);
      System.out.println("Success to insert torrent!");
    }
    catch( SQLException e )
    {
      System.out.println("[ERROR] Fail to insert torrent!");
      e.printStackTrace();
    }
  }
  
  public void deletepeerinfo(String peerip, String peerport)
  {
    try
    {
      stmt = con.createStatement();
      sql = "DELETE FROM PEERINFO "
          + "WHERE PEERIP = '" + peerip + "'"
          + "AND PEERPORT = '" + peerport + "'";
      stmt.executeUpdate(sql);
    }
    catch( SQLException e )
    {
      System.out.println("Fail to delete peer info!");
      e.printStackTrace();
    }
  }
  
  public void deletetorrent(String filename, int size, String hash)
  {
    try
    {
      
      String sizestr = Integer.toString(size);
      stmt = con.createStatement();
      sql = "DELETE FROM TORRENTINFO "
          + "WHERE FILENAME = '" + filename + "' "
          + "AND SIZE = '" + sizestr + "' "
          + "AND HASH = '" + hash + "'";
      stmt.executeUpdate(sql);
      System.out.println("Success to delete torrent!");
    }
    catch( SQLException e )
    {
      System.out.println("Fail to delete torrent!");
      e.printStackTrace();
    }
  }
  
  public void getpeer(ArrayList<User>userlist, String filename, int size, String hash)
  {
    try
    {
      System.out.println("[testfilename] " + filename);
      String sizestr = Integer.toString(size);
      stmt = con.createStatement();
      sql = "SELECT PEERIP, PEERPORT FROM PEERINFO "
          + "WHERE FILENAME = '" + filename +"' AND SIZE = '" + sizestr +"' AND HASH = '" + hash + "'";
      ResultSet rs = stmt.executeQuery(sql);
      while(rs.next())
      {
        String userip = rs.getString("PEERIP");
        int userport = rs.getInt("PEERPORT");
        System.out.println(userport);
        
        User user = new User(userport, userip); 
        userlist.add(user);
        
      }
      
    }
    catch( SQLException e )
    {
      System.out.println("Fail to delete peer info!");
      e.printStackTrace();
    }
  }
  
  public boolean verifytorrent(String filename, int size, String hash)
  {
    String sizestr = Integer.toString(size);
    boolean isexist = false;
    try
    {
      stmt = con.createStatement();
      sql = "SELECT * FROM TORRENTINFO WHERE "
          + "FILENAME = '"+ filename + "' AND SIZE = '" 
          + sizestr + "' AND HASH = '" + hash +  "'";
      ResultSet rs = stmt.executeQuery(sql);
      if(rs.next())
      {
        isexist = true;
      }
      System.out.println("Success to verify torrent info!");
      
    }
    catch( SQLException e )
    {
      System.out.println("Fail to verify torrent info!");
      e.printStackTrace();
    }
    return isexist;
  }
  
  
  
  
}
