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

public class WsGameplayHandler extends BaseHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler, WsErrorHandler {

  private final GameplayService service;
  private final HashMap<String, WsClient> clients = new HashMap<>();

  public WsGameplayHandler(Database db) {
    service = new GameplayService(db);
  }

  @Override
  public synchronized void handleConnect(WsConnectContext wcc) throws Exception {
    String authToken = wcc.header("Authorization");
    clients.put(wcc.sessionId(), new WsClient(wcc, authToken));
  }

  @Override
  public void handleMessage(WsMessageContext wmc) throws Exception {
    
  }

  @Override
  public void handleClose(WsCloseContext wcc) throws Exception {
    
  }

  @Override
  public void handleError(WsErrorContext wec) throws Exception {
    
  }

  private class WsClient {
    WsContext ctx;
    String authToken;

    public WsClient(WsContext ctx, String authToken) {
      this.ctx = ctx;
      this.authToken = authToken;
    }
  }
  
}
