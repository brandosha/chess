package dataaccess;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import chess.ChessGame;
import datamodel.GameData;

public class GameDaoSQL implements GameDao {
  private final String tableName = "games";

  Gson gson = new Gson();

  public GameDaoSQL() {
    DatabaseManager.forceCreateDatabase();

    var createTable = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
      + "  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
      + "  name VARCHAR(32) NOT NULL,"
      + "  whiteUsername VARCHAR(32),"
      + "  blackUsername VARCHAR(32),"
      + "  gameState VARCHAR(512) NOT NULL"
      + ")";
    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.createStatement()) {
        statement.executeUpdate(createTable);
      }
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void clear() {
    var delete = "DELETE FROM " + tableName;
    
    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.createStatement()) {
        statement.executeUpdate(delete);
      }
    } catch (Exception e) {
      // TODO: Proper error handling
      throw new RuntimeException(e);
    }
  }

  @Override
  public void insertGame(GameData game) {
    var insert = "INSERT INTO " + tableName
      + "  (name, whiteUsername, blackUsername, gameState)"
      + "  VALUES (?, ?, ?, ?);";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(insert)) {
        statement.setString(1, game.gameName);
        statement.setString(2, game.whiteUsername);
        statement.setString(3, game.blackUsername);
        statement.setString(4, gson.toJson(game.game));
        statement.executeUpdate();
      }
    } catch (Exception e) {
      // TODO: Proper error handling
      throw new RuntimeException(e);
    }
  }

  @Override
  public GameData getGame(int id) {
    var select = "SELECT id, name, whiteUsername, blackUsername, gameState FROM " + tableName
      + "  WHERE id = ?";

    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.prepareStatement(select)) {
        statement.setInt(1, id);
        var results = statement.executeQuery();
        if (results.next()) {
          var gameJson = results.getString(5);
          return new GameData(
            results.getInt(1),
            results.getString(2),
            results.getString(3),
            results.getString(4),
            gson.fromJson(gameJson, ChessGame.class)
          );
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    return null;
  }

  @Override
  public Collection<GameData> listGames() {
    var select = "SELECT id, name, whiteUsername, blackUsername, gameState FROM " + tableName;

    var list = new ArrayList<GameData>();
    try (var conn = DatabaseManager.getConnection()) {
      try (var statement = conn.createStatement()) {
        var results = statement.executeQuery(select);
        while (results.next()) {
          var gameJson = results.getString(5);
          var data = new GameData(
            results.getInt(1),
            results.getString(2),
            results.getString(3),
            results.getString(4),
            gson.fromJson(gameJson, ChessGame.class)
          );

          list.add(data);
        }
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    return list;
  }

  @Override
  public int nextGameId() {
    // TODO Auto-generated method stub
    return 0;
  }
  
}
