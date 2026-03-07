package dataaccess;

import java.util.Collection;

import datamodel.GameData;

public class GameDaoSQL implements GameDao {
  private final String tableName = "games";

  public GameDaoSQL() {
    DatabaseManager.forceCreateDatabase();

    var createTable = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
      + "  id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,"
      + "  game VARCHAR(512)"
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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'clear'");
  }

  @Override
  public void insertGame(GameData game) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'insertGame'");
  }

  @Override
  public GameData getGame(int id) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'getGame'");
  }

  @Override
  public Collection<GameData> listGames() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'listGames'");
  }

  @Override
  public int nextGameId() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'nextGameId'");
  }
  
}
