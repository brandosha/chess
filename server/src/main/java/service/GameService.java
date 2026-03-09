package service;

import dataaccess.Database;
import datamodel.GameData;
import datamodel.http.CreateGameRequest;
import datamodel.http.CreateGameResponse;
import datamodel.http.InvalidRequestException;
import datamodel.http.JoinGameRequest;
import datamodel.http.ListGamesResponse;

public class GameService extends BaseService {

  public GameService(Database db) {
    super(db);
  }

  public CreateGameResponse createGame(CreateGameRequest req, String authToken) throws InvalidRequestException, UnauthorizedException {
    req.validate();
    checkAuth(authToken);

    var newGame = db.gameDao.createGame(
      new GameData(null, null, null, req.gameName, null)
    );

    return new CreateGameResponse(newGame.gameID);
  }

  public ListGamesResponse listGames(String authToken) throws UnauthorizedException {
    checkAuth(authToken);
    var games = db.gameDao.listGames();
    return new ListGamesResponse(games);
  }

  public void joinGame(JoinGameRequest req, String authToken) throws InvalidRequestException, UnauthorizedException, AlreadyTakenException {
    req.validate();
    var auth = checkAuth(authToken);
    
    var dao = db.gameDao;
    var game = dao.getGame(req.gameID);
    if (req.playerColor.equals("BLACK")) {
      if (game.blackUsername != null) { throw new AlreadyTakenException("already taken"); }
      game.blackUsername = auth.username;
    } else {
      if (game.whiteUsername != null) { throw new AlreadyTakenException("already taken"); }
      game.whiteUsername = auth.username;
    }
    dao.updateGame(game);
  }
}
