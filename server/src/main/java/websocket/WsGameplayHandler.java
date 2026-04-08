package websocket;

import com.google.gson.Gson;

import dataaccess.DataAccessException;
import dataaccess.Database;
import datamodel.UserData;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsErrorContext;
import io.javalin.websocket.WsErrorHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import service.UnauthorizedException;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import static websocket.messages.ServerMessage.ServerMessageType.ERROR;
import static websocket.messages.ServerMessage.ServerMessageType.NOTIFICATION;

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
        case LEAVE -> leaveGame(wmc, cmd);
      }

    } catch (Exception e) {
      System.err.println(e);

      var error = new ServerMessage(ERROR);
      error.errorMessage = e.getMessage();
      wmc.send(gson.toJson(error));
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
  

  private void connectToGame(WsMessageContext wmc, UserGameCommand cmd) throws DataAccessException, UnauthorizedException {
    var user = checkAuth(cmd);
    var username = user.username;
    var game = db.gameDao.getGame(cmd.getGameID());
    
    var gameChannel = "game/" + cmd.getGameID();
    var notif = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
    
    if (username.equals(game.blackUsername)) {
      notif.message = username + " joined the game as black";
    } else if (username.equals(game.whiteUsername)) {
      notif.message = username + " joined the game as white";
    } else {
      notif.message = username + " is observing";
    }
    clients.broadcast(gameChannel, gson.toJson(notif));
    clients.subscribeClient(wmc, gameChannel);

    var loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
    loadGame.game = game;
    wmc.send(gson.toJson(loadGame));
  }

  private void leaveGame(WsMessageContext wmc, UserGameCommand cmd) throws DataAccessException, UnauthorizedException {
    var user = checkAuth(cmd);
    var username = user.username;

    var game = db.gameDao.getGame(cmd.getGameID());
    var notif = new ServerMessage(NOTIFICATION);
    notif.message = username + " left the game";
    if (username.equals(game.blackUsername)) {
      game.blackUsername = null;
      db.gameDao.updateGame(game);
    } else if (username.equals(game.whiteUsername)) {
      game.whiteUsername = null;
      db.gameDao.updateGame(game);
    } else {
      // throw new UnauthorizedException();
      notif.message = username + " is no longer observing";
    }

    var gameChannel = "game/" + cmd.getGameID();
    clients.unsubscribeClient(wmc, gameChannel);
    clients.broadcast(gameChannel, gson.toJson(notif));
  }

  private UserData checkAuth(UserGameCommand cmd) throws DataAccessException, UnauthorizedException {
    var user = db.userDao.getAuthUser(cmd.getAuthToken());
    if (user == null) {
      throw new UnauthorizedException();
    }

    return user;
  }
}
