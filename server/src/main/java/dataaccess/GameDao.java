package dataaccess;

import datamodel.GameData;

public interface GameDao extends DataAccessObject {
  public void insertGame(GameData game);
  public GameData getGame(int id);

  public int nextGameId();
}
