package service;

import dataaccess.Database;
import datamodel.GameData;
import datamodel.http.CreateGameRequest;
import datamodel.http.CreateGameResponse;
import datamodel.http.InvalidRequestException;

public class GameService extends BaseService {

  public GameService(Database db) {
    super(db);
  }

  public CreateGameResponse createGame(CreateGameRequest req, String authToken) throws InvalidRequestException, UnauthorizedException {
    req.validate();
    checkAuth(authToken);

    var dao = db.gameDao();
    var newId = dao.nextGameId();
    var newGame = new GameData(newId, null, null, req.gameName, null);

    dao.insertGame(newGame);

    return new CreateGameResponse(newId);
  }
}
