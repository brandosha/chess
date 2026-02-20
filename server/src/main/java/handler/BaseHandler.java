package handler;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import datamodel.http.InvalidRequestException;
import datamodel.http.Request;
import io.javalin.http.Context;

public class BaseHandler {
  final Gson gson;

  public BaseHandler() {
    this.gson = new Gson();
  }

  public <T extends Request> T parseRequest(Context ctx, Class<T> reqClass) throws JsonSyntaxException, InvalidRequestException {
    var json = ctx.body();
    var req = gson.fromJson(json, reqClass);
    req.validate();
    return req;
  }
}
