package dataaccess;

import java.util.Collection;

import datamodel.GameData;

public interface GameDao extends DataAccessObject {
  public GameData createGame(GameData game) throws DataAccessException;
  public GameData updateGame(GameData game) throws DataAccessException;
  public GameData getGame(int id) throws DataAccessException;
  public Collection<GameData> listGames() throws DataAccessException;
}
