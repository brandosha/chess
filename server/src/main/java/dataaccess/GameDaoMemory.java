package dataaccess;

import java.util.Collection;
import java.util.HashMap;

import datamodel.GameData;

public class GameDaoMemory implements GameDao {
  private final HashMap<Integer, GameData> games = new HashMap<>();

  @Override
  public GameData createGame(GameData game) {
    assert game.gameID == null : "GameData passed to createGame must have a null gameID";

    var nextId = games.size();
    games.put(nextId, game);

    game.gameID = nextId;
    return game;
  }

  @Override
  public GameData updateGame(GameData game) {
    return games.put(game.gameID, game);
  }

  @Override
  public GameData getGame(int id) {
    return games.get(id);
  }

  @Override
  public Collection<GameData> listGames() {
    return games.values();
  }

  @Override
  public void clear() {
    games.clear();
  }
}
