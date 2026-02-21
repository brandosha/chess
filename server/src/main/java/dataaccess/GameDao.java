package dataaccess;

import java.util.Collection;

import datamodel.GameData;

public interface GameDao extends DataAccessObject {
  public void insertGame(GameData game);
  public GameData getGame(int id);
  public Collection<GameData> listGames();

  public int nextGameId();
}
