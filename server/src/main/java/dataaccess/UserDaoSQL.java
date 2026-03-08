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
    var deleteAuth = "DELETE FROM " + authTableName;
    var deleteUsers = "DELETE FROM " + usersTableName;

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.createStatement()) {
        statement.execute(deleteAuth);
        statement.execute(deleteUsers);
      }
    } catch (Exception e) {
      // TODO: Proper error handling
      throw new RuntimeException(e);
    }
  }

  @Override
  public void insertUser(UserData user) {
    var insert = "INSERT INTO " + usersTableName + " (username, password, email) VALUES (?, ?, ?);";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(insert)) {
        statement.setString(1, user.username);
        statement.setString(2, user.password);
        statement.setString(3, user.email);
        statement.executeUpdate();
      }
    } catch (Exception e) {
      // TODO: Proper error handling
      throw new RuntimeException(e);
    }
  }

  @Override
  public UserData getUser(String username) {
    var select = "SELECT username, password, email FROM " + usersTableName + " WHERE username = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(select)) {
        statement.setString(1, username);
        var results = statement.executeQuery();
        if (results.next()) {
          return new UserData(
            results.getString(1),
            results.getString(2),
            results.getString(3)
          );
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    return null;
  }

  @Override
  public void insertAuth(AuthData auth) {
    var insert = "INSERT INTO " + authTableName + " (token, username) VALUES (?, ?);";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(insert)) {
        statement.setString(1, auth.authToken);
        statement.setString(2, auth.username);
        statement.executeUpdate();
      }
    } catch (Exception e) {
      // TODO: Proper error handling
      throw new RuntimeException(e);
    }
  }

  @Override
  public AuthData getAuth(String authToken) {
    var select = "SELECT token, username FROM " + authTableName + " WHERE token = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(select)) {
        statement.setString(1, authToken);
        var results = statement.executeQuery();
        if (results.next()) {
          return new AuthData(
            results.getString(1),
            results.getString(2)
          );
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    return null;
  }

  @Override
  public AuthData deleteAuth(String authToken) {
    var delete = "DELETE FROM " + authTableName + " WHERE token = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(delete)) {
        statement.setString(1, authToken);
        statement.executeUpdate();
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    return null;
  }

  @Override
  public UserData getAuthUser(String authToken) {
    var select = "SELECT u.username, u.password, u.email"
      + "  FROM " + authTableName + " a"
      + "  INNER JOIN " + usersTableName + " u ON"
      + "    (u.username = u.username)"
      + "  WHERE token = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(select)) {
        statement.setString(1, authToken);
        var results = statement.executeQuery();
        if (results.next()) {
          return new UserData(
            results.getString(1),
            results.getString(2),
            results.getString(3)
          );
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    return null;
  }

}