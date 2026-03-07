package dataaccess;

import datamodel.AuthData;
import datamodel.UserData;

public class UserDaoSQL implements UserDao {
  private final String usersTableName = "users";
  private final String authTableName = "authTokens";

  public UserDaoSQL() {
    DatabaseManager.forceCreateDatabase();

    var createUsersTable = "CREATE TABLE IF NOT EXISTS " + usersTableName + " ("
      + "  username VARCHAR(32) NOT NULL PRIMARY KEY,"
      + "  password VARCHAR(128) NOT NULL,"
      + "  email VARCHAR(64) NOT NULL"
      + ")";
    
    var createAuthTable = "CREATE TABLE IF NOT EXISTS " + authTableName + " ("
      + "  token VARCHAR(32) NOT NULL PRIMARY KEY,"
      + "  username VARCHAR(128) NOT NULL,"
      + "  FOREIGN KEY (username) REFERENCES " + usersTableName + "(username)"
      + ")";
    
    try (var conn = DatabaseManager.getConnection()) {
      conn.setAutoCommit(false);
      try (var statement = conn.createStatement()) {
        statement.executeUpdate(createUsersTable);
      }
      try (var statement = conn.createStatement()) {
        statement.executeUpdate(createAuthTable);
      }

      conn.commit();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void clear() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'clear'");
  }

  @Override
  public void insertUser(UserData user) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertUser'");
  }

  @Override
  public UserData getUser(String username) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getUser'");
  }

  @Override
  public void insertAuth(AuthData auth) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertAuth'");
  }

  @Override
  public AuthData getAuth(String authToken) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAuth'");
  }

  @Override
  public AuthData deleteAuth(String authToken) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'deleteAuth'");
  }

  @Override
  public UserData getAuthUser(String authToken) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getAuthUser'");
  }

}