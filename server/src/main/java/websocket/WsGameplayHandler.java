package websocket;

import com.google.gson.Gson;

import dataaccess.Database;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsErrorHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WsGameplayHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler {

  private final Database db;
  private final Gson gson = new Gson();
  private final WsClients clients = new WsClients();

  public WsGameplayHandler(Database db) {
    this.db = db;
  }

  @Override
  public synchronized void handleConnect(WsConnectContext wcc) throws Exception {
    wcc.enableAutomaticPings();
    clients.addClient(wcc);
    System.out.println("New WebSocket client: " + wcc.sessionId());
  }

  @Override
  public void handleMessage(WsMessageContext wmc) throws Exception {
    var msg = wmc.message();
    System.out.println("WebSocket message: " + msg);
    try {
      var cmd = gson.fromJson(msg, UserGameCommand.class);

      switch (cmd.getCommandType()) {
        case CONNECT -> connectToGame(wmc, cmd);
      }

    } catch (Exception e) {
      System.err.println(e);
    }
  }

  @Override
  public void handleClose(WsCloseContext wcc) throws Exception {
    clients.removeClient(wcc);
  }

  @Override
  public void handleError(WsErrorContext wec) throws Exception {
    clients.removeClient(wec);
    System.err.println("WebSocket error: " + wec.error().getMessage());
  }
  

  private void connectToGame(WsMessageContext wmc, UserGameCommand cmd) {
    var gameChannel = "game/" + cmd.getGameID();
    var notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    clients.broadcast(gameChannel, gson.toJson(notif));
    clients.subscribeClient(wmc, gameChannel);
    
    var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    wmc.send(gson.toJson(loadGame));
  }
}
