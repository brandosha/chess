package dataaccess;

public abstract class Database {
  public final UserDao userDao;
  public final GameDao gameDao;

  public Database(UserDao userDao, GameDao gameDao) {
    this.userDao = userDao;
    this.gameDao = gameDao;
  }

  public void clear() {
    userDao.clear();
    gameDao.clear();
  }
}
