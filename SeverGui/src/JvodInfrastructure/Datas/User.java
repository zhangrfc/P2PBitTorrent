package JvodInfrastructure.Datas;


import java.io.Serializable;

public class User implements Serializable{  
  
  public int port;
  public String ip;
  public User(int _port, String _ip){
    port = _port;
    ip= _ip;
  }
}
