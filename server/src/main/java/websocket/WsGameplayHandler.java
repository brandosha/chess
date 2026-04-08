package websocket;

import com.google.gson.Gson;

import chess.ChessGame;
import chess.ChessPiece;
import chess.InvalidMoveException;
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
import static websocket.messages.ServerMessage.ServerMessageType.LOAD_GAME;
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
        case MAKE_MOVE -> makeMove(wmc, cmd);
        case RESIGN -> resign(wmc, cmd);
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

  private void makeMove(WsMessageContext wmc, UserGameCommand cmd) throws DataAccessException, UnauthorizedException, InvalidMoveException {
    var user = checkAuth(cmd);
    var username = user.username;

    var gdata = db.gameDao.getGame(cmd.getGameID());
    var game = gdata.game;
    if (gdata.gameOver) {
      throw new UnauthorizedException("The game is over");
    }

    ChessGame.TeamColor userTeam = null;
    if (username.equals(gdata.blackUsername)) {
      userTeam = ChessGame.TeamColor.BLACK;
    } else if (username.equals(gdata.whiteUsername)) {
      userTeam = ChessGame.TeamColor.WHITE;
    }

    if (userTeam == null) {
      throw new UnauthorizedException();
    } else if (userTeam != game.getTeamTurn()) {
      throw new UnauthorizedException("Not your turn");
    }

    var move = cmd.move;
    var piece = game.getBoard().getPiece(move.getStartPosition());
    game.makeMove(move);

    db.gameDao.updateGame(gdata);
    var gameChannel = "game/" + cmd.getGameID();
    var loadGame = new ServerMessage(LOAD_GAME);
    loadGame.game = gdata;
    clients.broadcast(gameChannel, gson.toJson(loadGame));

    var notif = new ServerMessage(NOTIFICATION);
    notif.message = username + " moved " + ChessPiece.name(piece) + " to " + move.getEndPosition().name();
    clients.broadcast(gameChannel, gson.toJson(notif), wmc);

    var otherTeam = game.getTeamTurn();
    notif.message = null;
    if (game.isInCheckmate(otherTeam)) {
      notif.message = "checkmate";
    } else if (game.isInStalemate(otherTeam)) {
      notif.message = "stalemate";
    } else if (game.isInCheck(otherTeam)) {
      notif.message = "check";
    }

    if (notif.message != null) {
      clients.broadcast(gameChannel, gson.toJson(notif));
    }
  }

  private void resign(WsMessageContext wmc, UserGameCommand cmd) throws DataAccessException, UnauthorizedException {
    var user = checkAuth(cmd);
    var username = user.username;

    var game = db.gameDao.getGame(cmd.getGameID());
    if (game.gameOver) {
      throw new UnauthorizedException("The game is over");
    }

    if (username.equals(game.blackUsername) || username.equals(game.whiteUsername)) {
      game.gameOver = true;
      db.gameDao.updateGame(game);
    } else {
      throw new UnauthorizedException();
    }
    
    var notif = new ServerMessage(NOTIFICATION);
    notif.message = username + " resigned";

    var gameChannel = "game/" + cmd.getGameID();
    clients.broadcast(gameChannel, gson.toJson(notif));
    clients.unsubscribeClient(wmc, gameChannel);
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
