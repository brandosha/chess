package service;

import dataaccess.Database;

public abstract class BaseService {
  final Database db;

  public BaseService(Database db) {
    this.db = db;
  }
}
