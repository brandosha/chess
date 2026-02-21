package handler;

import dataaccess.Database;
import datamodel.http.CreateGameRequest;
import datamodel.http.JoinGameRequest;
import io.javalin.http.Context;
import service.GameService;

public class GameHandler extends BaseHandler {
  GameService service;

  public GameHandler(Database db) {
    super();
    this.service = new GameService(db);
  }

  public void createGame(Context ctx) throws Exception {
    var authToken = ctx.header("Authorization");
    var req = parseRequest(ctx, CreateGameRequest.class);
    var res = service.createGame(req, authToken);
    ctx.result(gson.toJson(res));
  }

  public void listGames(Context ctx) throws Exception {
    var authToken = ctx.header("Authorization");
    var res = service.listGames(authToken);
    ctx.result(gson.toJson(res));
  }

  public void joinGame(Context ctx) throws Exception {
    var authToken = ctx.header("Authorization");
    var req = parseRequest(ctx, JoinGameRequest.class);
    service.joinGame(req, authToken);
    ctx.result("{}");
  }
}
