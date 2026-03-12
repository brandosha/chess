package dataaccess;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import chess.ChessGame;
import datamodel.GameData;

public class GameDaoTests {
  GameDao dao = new GameDaoSQL();

  @Test
  public void createGamePositive() throws DataAccessException {
    var game = new GameData(null, null, null, "gname", new ChessGame());
    var result = dao.createGame(game);
    assertNotEquals(null, result);

    var stored = dao.getGame(result.gameID);
    assertEquals(result, stored);
  }

  @Test
  public void createGameNegative() {
    var game1 = new GameData(2, null, null, "gname", new ChessGame());
    assertThrows(DataAccessException.class, () -> dao.createGame(game1));

    var game2 = new GameData(null, null, null, null, new ChessGame());
    assertThrows(DataAccessException.class, () -> dao.createGame(game2));
  }

  @Test
  public void updateGamePositive() throws DataAccessException {
    var chess = new ChessGame();
    var game = new GameData(null, null, null, "gname", chess);
    game = dao.createGame(game);

    dao.updateGame(game);
  }

  @Test
  public void updateGameNegative() {
    var game1 = new GameData(null, null, null, "gname", new ChessGame());
    assertThrows(DataAccessException.class, () -> dao.updateGame(game1));
  }

  @Test
  public void getGamePositive() throws DataAccessException {
    var game = new GameData(null, null, null, "gname", new ChessGame());
    game = dao.createGame(game);

    assertEquals(game, dao.getGame(game.gameID));
  }

  @Test
  public void getGameNegative() throws DataAccessException {
    var game = new GameData(-1, null, null, "gname", new ChessGame());
    assertThrows(DataAccessException.class, () -> dao.updateGame(game));
  }

  @Test
  public void testListGames() throws DataAccessException {
    dao.clear();
    GameData[] games = {
      new GameData(null, null, null, "game1", new ChessGame()),
      new GameData(null, null, null, "game2", new ChessGame()),
      new GameData(null, null, null, "game3", new ChessGame())
    };

    var gamesSet = new HashSet<GameData>();
    for (int i = 0; i < games.length; ++i) {
      gamesSet.add(dao.createGame(games[i]));
    }

    var storedGames = dao.listGames();
    assertEquals(gamesSet, new HashSet<GameData>(storedGames));
  }

  @Test
  public void testClear() throws DataAccessException {
    dao.clear();
  }
}
