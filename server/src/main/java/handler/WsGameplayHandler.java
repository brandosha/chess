package handler;

import java.util.HashMap;

import dataaccess.Database;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsErrorHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.GameplayService;
import websocket.commands.UserGameCommand;

public class WsGameplayHandler extends BaseHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler {

  private final GameplayService service;
  private final HashMap<String, WsClient> clients = new HashMap<>();

  public WsGameplayHandler(Database db) {
    service = new GameplayService(db);
  }

  @Override
  public synchronized void handleConnect(WsConnectContext wcc) throws Exception {
    clients.put(wcc.sessionId(), new WsClient(wcc));
    System.out.println("New websocket client: " + wcc.sessionId());
  }

  @Override
  public void handleMessage(WsMessageContext wmc) throws Exception {
    var msg = wmc.message();
    try {
      var cmd = gson.fromJson(msg, UserGameCommand.class);

      switch (cmd.getCommandType()) {
        case CONNECT -> service.connectToGame(cmd.getGameID(), cmd.getAuthToken());
      }

    } catch (Exception e) {
      System.err.println(e);
    }
  }

  @Override
  public void handleClose(WsCloseContext wcc) throws Exception {
    
  }

  @Override
  public void handleError(WsErrorContext wec) throws Exception {
    
  }

  private class WsClient {
    final WsContext ctx;

    public WsClient(WsContext ctx) {
      this.ctx = ctx;
    }
  }
  
}
