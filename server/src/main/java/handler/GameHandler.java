package handler;

import dataaccess.Database;
import datamodel.http.CreateGameRequest;
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
}
