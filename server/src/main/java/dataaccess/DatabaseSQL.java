package dataaccess;

public class DatabaseSQL extends Database {

  public DatabaseSQL() {
    super(
      new UserDaoSQL(),
      new GameDaoSQL()
    );
  }
}
