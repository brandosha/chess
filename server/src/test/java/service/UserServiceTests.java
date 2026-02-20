package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dataaccess.Database;
import dataaccess.DatabaseMemory;
import datamodel.http.RegisterRequest;

public class UserServiceTests {

  UserService service;
  Database db;

  @BeforeEach
  void beforeEach() {
    db = new DatabaseMemory();
    service = new UserService(db);
  }
  
  @Test
  public void registerPositive() throws Exception {
    var req = new RegisterRequest("username", "password", "email");
    var res = service.register(req);

    assert res.username.equals(req.username);
  }

  @Test
  public void registerNegative() throws Exception {
    
  }
}
