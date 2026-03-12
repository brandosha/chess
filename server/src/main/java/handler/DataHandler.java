package handler;

import dataaccess.DataAccessException;
import dataaccess.Database;
import io.javalin.http.Context;

public class DataHandler extends BaseHandler {
  final Database db;

  public DataHandler(Database db) {
    super();
    this.db = db;
  }

  public void clearDb(Context ctx) throws DataAccessException {
    db.clear();
    ctx.result("{}");
  }
}
