package handler;

import dataaccess.Database;
import datamodel.http.LoginRequest;
import datamodel.http.RegisterRequest;
import io.javalin.http.Context;
import service.UserService;

public class UserHandler extends BaseHandler {

  UserService service;

  public UserHandler(Database db) {
    super();
    this.service = new UserService(db);
  }
  
  public void register(Context ctx) throws Exception {
    var req = parseRequest(ctx, RegisterRequest.class);
    var res = service.register(req);
    ctx.result(gson.toJson(res));
  }

  public void login(Context ctx) throws Exception {
    var req = parseRequest(ctx, LoginRequest.class);
    var res = service.login(req);
    ctx.result(gson.toJson(res));
  }
}
