package dataaccess;

public interface Database {
  public UserDao userDao();
  public GameDao gameDao();

  public void clear();
}
