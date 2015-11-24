package ServerJDBC;

public class Droptable
{
  public static void main(String[] argc)
  {
    System.out.println("Drop Tables");
    Database gamedb = new Database();
    try
    {
      gamedb.connect();
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }

    gamedb.droptable();

    gamedb.disconnect();
  }
}
