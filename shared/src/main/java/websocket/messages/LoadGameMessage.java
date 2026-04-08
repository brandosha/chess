package websocket.messages;

import datamodel.GameData;

public class LoadGameMessage extends ServerMessage {

  final GameData game;

  public LoadGameMessage(GameData game) {
    super(ServerMessageType.LOAD_GAME);
    this.game = game;
  }
  
}
