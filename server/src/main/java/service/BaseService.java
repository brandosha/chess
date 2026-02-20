package service;

import dataaccess.Database;
import datamodel.AuthData;

public abstract class BaseService {
  final Database db;

  public BaseService(Database db) {
    this.db = db;
  }

  public AuthData checkAuth(String authToken) throws UnauthorizedException {
    var user = db.userDao().getAuth(authToken);
    if (user == null) {
      throw new UnauthorizedException();
    }

    return user;
  }
}
