package dataaccess;

public class DatabaseMemory implements Database {

  final UserDao userDao;
  final GameDao gameDao;

  public DatabaseMemory() {
    userDao = new UserDaoMemory();
    gameDao = new GameDaoMemory();
  }

  @Override
  public UserDao userDao() {
    return userDao;
  }

  @Override
  public GameDao gameDao() {
    return gameDao;
  }

  @Override
  public void clear() {
    userDao.clear();
    gameDao.clear();
  }
}
