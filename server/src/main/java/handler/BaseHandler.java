package handler;

import com.google.gson.Gson;

import datamodel.http.Request;
import io.javalin.http.Context;

public class BaseHandler {
  final Gson gson;

  public BaseHandler() {
    this.gson = new Gson();
  }

  public <T extends Request> T parseRequest(Context ctx, Class<T> reqClass) throws Exception {
    var json = ctx.body();
    var req = gson.fromJson(json, reqClass);
    req.validate();
    return req;
  }
}
