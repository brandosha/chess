package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.DatabaseMemory;
import datamodel.http.RegisterRequest;

public class UserServiceTests {

  UserService service;

  @BeforeEach
  void beforeEach() {
    var db = new DatabaseMemory();
    this.service = new UserService(db);
  }
  
  @Test
  public void registerPositive() throws Exception {
    var req = new RegisterRequest("username", "password", "email");
    var res = service.register(req);

    assert res.username.equals(req.username);
  }
}
