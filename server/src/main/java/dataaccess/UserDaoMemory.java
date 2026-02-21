package dataaccess;

import java.util.HashMap;

import datamodel.AuthData;
import datamodel.UserData;

public class UserDaoMemory implements UserDao {
  private final HashMap<String, UserData> users = new HashMap<>();
  private final HashMap<String, AuthData> authTokens = new HashMap<>();

  @Override
  public void insertUser(UserData user) {
    users.put(user.username, user);
  }

  @Override
  public UserData getUser(String username) {
    return users.get(username);
  }

  @Override
  public void insertAuth(AuthData auth) {
    authTokens.put(auth.authToken, auth);
  }

  @Override
  public AuthData getAuth(String authToken) {
    return authTokens.get(authToken);
  }

  @Override
  public AuthData deleteAuth(String authToken) {
    return authTokens.remove(authToken);
  }

  @Override
  public UserData getAuthUser(String authToken) {
    var auth = authTokens.get(authToken);
    if (auth == null) { return null; }
    return users.get(auth.username);
  }

  @Override
  public void clear() {
    users.clear();
    authTokens.clear();
  }
}
