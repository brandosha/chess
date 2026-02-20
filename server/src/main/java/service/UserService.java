package service;

import dataaccess.Database;
import datamodel.AuthData;
import datamodel.http.RegisterRequest;
import datamodel.http.RegisterResponse;

public class UserService extends BaseService {
  public UserService(Database db) {
    super(db);
  }

  public RegisterResponse register(RegisterRequest req) throws AlreadyTakenException {
    var dao = db.userDao();
    
    var existing = dao.getUser(req.username);
    if (existing != null) {
      throw new AlreadyTakenException("Username already taken");
    } else {
      var newUser = req.userData();
      dao.insertUser(newUser);
      var newAuth = AuthData.generate(newUser.username);
      dao.insertAuth(newAuth);
      return new RegisterResponse(newAuth);
    }
  }
}
