package client.views;

import java.io.IOException;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.WebSocketFacade;
import datamodel.GameData;
import jakarta.websocket.DeploymentException;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

public class ObserveGameView extends ReplView {

  final ServerFacade serverFacade;
  WebSocketFacade wsFacade;
  final String authToken;
  GameData game;

  public ObserveGameView(ServerFacade serverFacade, String authToken, GameData game) {
    this.serverFacade = serverFacade;
    this.authToken = authToken;
    this.game = game;
  }

  @Override
  public void onAppear() {
    try {
      wsFacade = serverFacade.webSocket();
      wsFacade.connectToGame(game.gameID, authToken, this::recvMessage);
      help();
    } catch (DeploymentException | IOException e) {
      console.printf("An error occurred while connecting to the game:\n%s\n\n", e.getMessage());
      close();
    }
  }

  @Override
  public void close() {
    try {
      wsFacade.leaveGame(game.gameID, authToken);
      wsFacade.disconnect();
      super.close();
    } catch (IOException e) {
      console.printf("Failed to disconnect:\n%s\n", e.getMessage());
    }
  }

  void recvMessage(ServerMessage msg) {
    switch (msg.getServerMessageType()) {
      case LOAD_GAME -> {
        this.game = msg.game;
        draw();
      }
      case NOTIFICATION -> {
        var message =
          EscapeSequences.SET_TEXT_COLOR_GREEN +
          "> " + msg.message +
          EscapeSequences.RESET_TEXT_COLOR;
        
        System.out.printf("\n%s\n> ", message);
      }
      case ERROR -> {
        var error =
          EscapeSequences.SET_TEXT_COLOR_RED +
          "> Error: " + msg.errorMessage +
          EscapeSequences.RESET_TEXT_COLOR;

        System.out.printf("\n%s\n> ", error);
      }
    }
  }

  @Override
  public void rep() {
    var argv = readCmd("> ");
    if (argv == null) {
      controller.stop();
      return;
    }

    switch (argv[0]) {
      case "d", "draw" -> draw();
      case "s", "stop" -> close();
      case "h", "help" -> help();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  public void draw() {
    // We can't use console because it's synchronized but this should
    // be asynchrounous because it's drawn in response to a WebSocket event
    System.out.printf("%s\n> ", gameBoardString(game, ChessGame.TeamColor.WHITE));
    System.out.flush();
  }

  public void help() {
    String helpText = """

        [d]raw                | Redraw the game board
        [h]elp                | Show this help message
        [s]top                | Stop observing

      """;
    
    console.printf(helpText);
  }

  public static String gameBoardString(GameData g, ChessGame.TeamColor perspective) {
    ChessBoard board = g.game.getBoard();
    String s = "\n";

    int height = ChessBoard.HEIGHT;
    int width = ChessBoard.WIDTH;

    String files = "abcdefgh";

    s += "  ";
    for (int c = 0; c < width; c++) {
      int col = c + 1;
      if (perspective == ChessGame.TeamColor.BLACK) {
        col = width - c;
      }
      s += " " + files.charAt(col - 1) + " ";
    }
    s += "\n";

    for (int r = 0; r < height; r++) {
      int row = height - r;
      if (perspective == ChessGame.TeamColor.BLACK) {
        row = r + 1;
      }
      
      s += row + " ";
      for (int c = 0; c < width; c++) {
        int col = c + 1;
        if (perspective == ChessGame.TeamColor.BLACK) {
          col = width - c;
        }

        var pos = new ChessPosition(row, col);
        if ((row + col) % 2 == 0) {
          s += EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        } else {
          s += EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
        }

        var piece = board.getPiece(pos);
        if (piece == null) {
          s += "   ";
          continue;
        }

        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
          s += EscapeSequences.SET_TEXT_COLOR_BLACK;
        } else {
          s += EscapeSequences.SET_TEXT_COLOR_WHITE;
        }
        s += " " + ChessPiece.boardString(piece) + " ";
      }
      s += EscapeSequences.RESET_BG_COLOR;
      s += EscapeSequences.RESET_TEXT_COLOR;
      s += " " + row;
      s += "\n";
    }

    s += "  ";
    for (int c = 0; c < width; c++) {
      int col = c + 1;
      if (perspective == ChessGame.TeamColor.BLACK) {
        col = width - c;
      }
      s += " " + files.charAt(col - 1) + " ";
    }
    s += "\n\n";

    return s;
  }
  
}
