package handler;

import com.google.gson.Gson;

import dataaccess.UserDao;
import datamodel.http.RegisterRequest;
import io.javalin.http.Context;
import service.UserService;

public class UserHandler {

  UserService service;
  Gson gson;

  public UserHandler(UserDao dao) {
    this.service = new UserService(dao);
    this.gson = new Gson();
  }
  
  public void register(Context ctx) throws Exception {
    String json = ctx.body();
    var req = gson.fromJson(json, RegisterRequest.class);
    var res = service.register(req);
    ctx.result(gson.toJson(res));
  }
}
