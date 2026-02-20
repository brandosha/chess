package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.UserDao;
import dataaccess.UserDaoMemory;
import datamodel.http.RegisterRequest;

public class UserServiceTests {

  UserService service;

  @BeforeEach
  void beforeEach() {
    UserDao dao = new UserDaoMemory();
    this.service = new UserService(dao);
  }
  
  @Test
  public void registerPositive() throws Exception {
    var req = new RegisterRequest("username", "password", "email");
    var res = service.register(req);

    assert res.username.equals(req.username);
  }
}
