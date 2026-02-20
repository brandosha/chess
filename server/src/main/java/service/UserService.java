package service;

import dataaccess.UserDao;
import datamodel.AuthData;
import datamodel.http.RegisterRequest;
import datamodel.http.RegisterResponse;

public class UserService {
  public UserDao dao;

  public UserService(UserDao dao) {
    this.dao = dao;
  }

  public RegisterResponse register(RegisterRequest req) throws AlreadyTakenException {
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
