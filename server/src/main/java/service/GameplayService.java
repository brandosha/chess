package service;

import dataaccess.DataAccessException;
import dataaccess.Database;

public class GameplayService extends BaseService {

  public GameplayService(Database db) {
    super(db);
  }

  public void connectToGame(int gameID, String authToken) throws DataAccessException {
    var game = db.gameDao.getGame(gameID);
    
  }
  
}
