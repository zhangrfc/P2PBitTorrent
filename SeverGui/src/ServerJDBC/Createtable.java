package ServerJDBC;


public class Createtable
{
  public static void main(String[] argc)
  {
    System.out.println("Create Tables");
    String sql = "";
    Database gamedb = new Database();
    try
    {
      gamedb.connect();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    
    gamedb.createtable();
    
    gamedb.disconnect();
  }
}
