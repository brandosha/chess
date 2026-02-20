package service;

import dataaccess.Database;
import datamodel.AuthData;
import datamodel.http.InvalidRequestException;
import datamodel.http.LoginRequest;
import datamodel.http.LoginResponse;
import datamodel.http.RegisterRequest;
import datamodel.http.RegisterResponse;

public class UserService extends BaseService {
  public UserService(Database db) {
    super(db);
  }

  public RegisterResponse register(RegisterRequest req) throws InvalidRequestException, AlreadyTakenException {
    req.validate();
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

  public LoginResponse login(LoginRequest req) throws InvalidRequestException, UnauthorizedException {
    req.validate();
    var dao = db.userDao();

    var user = dao.getUser(req.username);
    if (user == null || !user.checkPassword(req.password)) {
      throw new UnauthorizedException("unauthorized");
    }

    var authData = AuthData.generate(user.username);
    dao.insertAuth(authData);
    return new LoginResponse(authData);
  }
}
