package dataaccess;

import java.util.Collection;

import datamodel.GameData;

public interface GameDao extends DataAccessObject {
  public GameData createGame(GameData game);
  public GameData updateGame(GameData game);
  public GameData getGame(int id);
  public Collection<GameData> listGames();
}
