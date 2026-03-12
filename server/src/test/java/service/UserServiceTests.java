package service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import dataaccess.Database;
import dataaccess.DatabaseMemory;
import datamodel.http.LoginRequest;
import datamodel.http.RegisterRequest;

public class UserServiceTests {

  UserService service;
  Database db;
  RegisterRequest defaultUser = new RegisterRequest("username", "password", "email");

  @BeforeEach
  public void beforeEach() {
    db = new DatabaseMemory();
    service = new UserService(db);
  }
  
  @Test
  public void registerPositive() throws Exception {
    var req = this.defaultUser;
    var res = service.register(req);

    var user = db.userDao.getUser(req.username);
    assertEquals(req.username, user.username);
    assertEquals(req.email, user.email);
    assertEquals(true, BCrypt.checkpw(req.password, user.password));

    var auth = db.userDao.getAuth(res.authToken);
    assertEquals(req.username, auth.username);
  }

  @Test
  public void registerNegative() throws Exception {
    service.register(defaultUser);

    assertThrows(AlreadyTakenException.class, () -> service.register(defaultUser));
  }

  @Test
  public void loginPositive() throws Exception {
    service.register(defaultUser);

    var res = service.login(new LoginRequest(defaultUser.username, defaultUser.password));
    var auth = db.userDao.getAuth(res.authToken);

    assertEquals(auth.username, defaultUser.username);
  }

  @Test
  public void loginNegative() throws Exception {
    var loginReq = new LoginRequest("no-exist", "");

    assertThrows(UnauthorizedException.class, () -> service.login(loginReq));
  }

  @Test
  public void logoutPositive() throws Exception {
    var res = service.register(defaultUser);
    service.logout(res.authToken);

    var auth = db.userDao.getAuthUser(res.authToken);
    assertEquals(null, auth);
  }

  @Test
  public void logoutNegative() throws Exception {
    assertThrows(UnauthorizedException.class, () -> service.logout("fake-auth-token"));
  }
}
