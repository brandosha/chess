package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

public interface UserDao extends DataAccessObject {
  public void insertUser(UserData user);
  public UserData getUser(String username);

  public void insertAuth(AuthData auth);
  public AuthData getAuth(String authToken);
  public AuthData deleteAuth(String authToken);
  public UserData getAuthUser(String authToken);
}
