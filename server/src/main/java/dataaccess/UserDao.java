package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

public interface UserDao extends DataAccessObject {
  public void insertUser(UserData user) throws DataAccessException;
  public UserData getUser(String username) throws DataAccessException;

  public void insertAuth(AuthData auth) throws DataAccessException;
  public AuthData getAuth(String authToken) throws DataAccessException;
  public AuthData deleteAuth(String authToken) throws DataAccessException;
  public UserData getAuthUser(String authToken) throws DataAccessException;
}
