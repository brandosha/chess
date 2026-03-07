package dataaccess;

public class DatabaseSQL implements Database {
  final UserDao userDao = new UserDaoSQL();
  final GameDao gameDao = new GameDaoSQL();

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
