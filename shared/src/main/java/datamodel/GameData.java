package datamodel;

import chess.ChessGame;

public class GameData {
  public int gameID;
  public String whiteUsername;
  public String blackUsername;
  public String gameName;
  public ChessGame game;

  public GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {
    this.gameID = gameID;
    this.whiteUsername = whiteUsername;
    this.blackUsername = blackUsername;
    this.gameName = gameName;
    this.game = game;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + gameID;
    result = prime * result + ((whiteUsername == null) ? 0 : whiteUsername.hashCode());
    result = prime * result + ((blackUsername == null) ? 0 : blackUsername.hashCode());
    result = prime * result + ((gameName == null) ? 0 : gameName.hashCode());
    result = prime * result + ((game == null) ? 0 : game.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) { return true; }
    if (obj == null) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    GameData other = (GameData) obj;
    if (gameID != other.gameID) { return false; }
    if (whiteUsername == null) {
      if (other.whiteUsername != null) { return false; }
    } else if (!whiteUsername.equals(other.whiteUsername)) { return false; }
    if (blackUsername == null) {
      if (other.blackUsername != null) { return false; }
    } else if (!blackUsername.equals(other.blackUsername)) { return false; }
    if (gameName == null) {
      if (other.gameName != null) { return false; }
    } else if (!gameName.equals(other.gameName)) { return false; }
    if (game == null) {
      if (other.game != null) { return false; }
    } else if (!game.equals(other.game)) { return false; }
    return true;
  }
}
