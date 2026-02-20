package dataaccess;

public class DatabaseMemory implements Database {

  final UserDao userDao;

  public DatabaseMemory() {
    userDao = new UserDaoMemory();
  }

  @Override
  public UserDao userDao() {
    return userDao;
  }
  
}
