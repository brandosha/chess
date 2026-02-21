package dataaccess;

import java.util.HashMap;

import datamodel.GameData;

public class GameDaoMemory implements GameDao {
  private final HashMap<Integer, GameData> games = new HashMap<>();
  private Integer nextKey = 0;

  @Override
  public int nextGameId() {
    nextKey += 1;
    return nextKey;
  }

  @Override
  public void insertGame(GameData game) {
    games.put(game.gameID, game);
  }

  @Override
  public GameData getGame(int id) {
    return games.get(id);
  }

  @Override
  public void clear() {
    games.clear();
  }
}
