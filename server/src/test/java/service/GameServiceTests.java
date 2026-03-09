package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.Database;
import dataaccess.DatabaseMemory;
import datamodel.UserData;
import datamodel.http.CreateGameRequest;
import datamodel.http.JoinGameRequest;
import datamodel.http.RegisterRequest;

public class GameServiceTests {
  GameService service;
  Database db;

  UserData defaultUser1 = new UserData("username1", "password", "email");
  String authToken1;

  UserData defaultUser2 = new UserData("username2", "password", "email");
  String authToken2;
  

  @BeforeEach
  public void beforeEach() throws Exception {
    db = new DatabaseMemory();
    service = new GameService(db);

    UserService userService = new UserService(db);
    var res1 = userService.register(
      new RegisterRequest(defaultUser1.username, defaultUser1.password, defaultUser1.email)
    );
    authToken1 = res1.authToken;

    var res2 = userService.register(
      new RegisterRequest(defaultUser2.username, defaultUser2.password, defaultUser2.email)
    );
    authToken2 = res2.authToken;
  }

  @Test
  public void createGamePositive() throws Exception {
    var req = new CreateGameRequest("new game");
    var res = service.createGame(req, authToken1);

    var game = db.gameDao.getGame(res.gameID);
    assertEquals(req.gameName, game.gameName);
  }

  @Test
  public void createGameNegative() throws Exception {
    var req = new CreateGameRequest("new game");
    assertThrows(UnauthorizedException.class, () -> service.createGame(req, "fake-auth-token"));
  }

  @Test
  public void listGamesPositive() throws Exception {
    var req = new CreateGameRequest("new game");
    var createRes = service.createGame(req, authToken1);
    var game = db.gameDao.getGame(createRes.gameID);

    var list = service.listGames(authToken1);
    assertEquals(1, list.games.size());
    assertEquals(game, list.games.iterator().next());
  }

  @Test
  public void listGamesNegative() throws Exception {
    assertThrows(UnauthorizedException.class, () -> service.listGames("fake-auth-token"));
  }

  @Test
  public void joinGamePositive() throws Exception {
    var createReq = new CreateGameRequest("new game");
    var createRes = service.createGame(createReq, authToken1);

    var req = new JoinGameRequest("BLACK", createRes.gameID);
    service.joinGame(req, authToken1);

    var game = db.gameDao.getGame(createRes.gameID);
    assertEquals(defaultUser1.username, game.blackUsername);
  }

  @Test
  public void joinGameNegative() throws Exception {
    var createReq = new CreateGameRequest("new game");
    var createRes = service.createGame(createReq, authToken1);

    var req = new JoinGameRequest("BLACK", createRes.gameID);
    service.joinGame(req, authToken1);
    
    assertThrows(AlreadyTakenException.class, () -> service.joinGame(req, authToken2));
  }
}
