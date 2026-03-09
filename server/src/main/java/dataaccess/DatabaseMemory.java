package dataaccess;

public class DatabaseMemory extends Database {

  public DatabaseMemory() {
    super(
      new UserDaoMemory(),
      new GameDaoMemory()
    );
  }
}
