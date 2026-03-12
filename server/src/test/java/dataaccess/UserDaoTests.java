package dataaccess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import datamodel.AuthData;
import datamodel.UserData;

public class UserDaoTests {
  UserDao dao = new UserDaoSQL();

  @Test
  public void insertUserPositive() throws DataAccessException {
    dao.clear();

    var user = new UserData("username", "pw", "email");
    dao.insertUser(user);

    var storedUser = dao.getUser(user.username);
    assertEquals(user, storedUser);
  }

  @Test
  public void insertUserNegative() throws DataAccessException {
    dao.clear();

    var user = new UserData("username", "pw", "email");
    dao.insertUser(user);

    assertThrows(DataAccessException.class, () -> dao.insertUser(user));
  }

  @Test
  public void getUserPositive() throws DataAccessException {
    dao.clear();

    var user = new UserData("gusername", "gpw", "gemail");
    dao.insertUser(user);

    var storedUser = dao.getUser(user.username);
    assertEquals(user, storedUser);
  }

  @Test
  public void getUserNegative() throws DataAccessException {
    dao.clear();

    assertEquals(null, dao.getUser("no-exist"));
  }

  @Test
  public void insertAuthPositive() throws DataAccessException {
    dao.clear();

    var user = new UserData("username", "pw", "email");
    dao.insertUser(user);
    var auth = AuthData.generate(user.username);
    dao.insertAuth(auth);

    assertEquals(auth, dao.getAuth(auth.authToken));
  }

  @Test
  public void insertAuthNegative() throws DataAccessException {
    dao.clear();

    var user = new UserData("username", "pw", "email");
    var auth = AuthData.generate(user.username);
    assertThrows(DataAccessException.class, () -> dao.insertAuth(auth));
  }

  @Test
  public void clear() throws DataAccessException {
    dao.clear();

    var user = new UserData("username", "pw", "email");
    dao.insertUser(user);

    dao.clear();
    assertEquals(null, dao.getUser(user.username));
  }
}
