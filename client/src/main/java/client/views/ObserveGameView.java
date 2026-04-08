package client.views;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
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
        drawWithPrompt();
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
      case "i", "highlight" -> highlight(argv);
      case "l", "leave" -> leave();
      case "h", "help" -> help();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  public void draw() {
    // We can't use console because it's synchronized but this should
    // be asynchrounous because it's drawn in response to a WebSocket event
    System.out.printf("%s\n", gameBoardString(game, ChessGame.TeamColor.WHITE));
  }

  public void drawWithPrompt() {
    // We can't use console because it's synchronized but this should
    // be asynchrounous because it's drawn in response to a WebSocket event
    draw();
    System.out.print("> ");
    System.out.flush();
  }

  public void highlight(String[] argv) {
    if (argv.length != 2) {
      console.printf("Usage: %s <square>\n", argv[0]);
    }

    var pos = parsePos(argv[1]);
    if (pos == null) {
      console.printf("'%s' is not a valid square\n", argv[1]);
      return;
    }

    var piece = game.game.getBoard().getPiece(pos);
    if (piece == null) {
      console.printf("There is no piece at square '%s'\n", argv[1]);
      return;
    }

    System.out.printf("%s\n> ", gameBoardString(game, ChessGame.TeamColor.WHITE, pos));
    System.out.flush();
  }

  public void leave() {
    try {
      wsFacade.leaveGame(game.gameID, authToken);
      Thread.sleep(300);
      close();
    } catch (Exception e) {
      console.printf("Failed to leave:\n%s\n", e.getMessage());
    }
  }

  public void help() {
    String helpText = """

        [d]raw                | Redraw the game board
        h[i]ghlight <square>  | Highlight moves for a piece at the given square (ex. e2)
        [h]elp                | Show this help message
        [l]eave               | Stop observing and go back

      """;
    
    console.printf(helpText);
  }

  public static String gameBoardString(GameData g, ChessGame.TeamColor perspective, ChessPosition highlight) {
    ChessBoard board = g.game.getBoard();
    String s = "\n";

    final int height = ChessBoard.HEIGHT;
    final int width = ChessBoard.WIDTH;

    final String files = "abcdefgh";

    Set<ChessPosition> highlightedSquares = new HashSet<>();
    if (highlight != null) {
      highlightedSquares.add(highlight);
      final var moves = g.game.validMoves(highlight);
      for (ChessMove move : moves) {
        highlightedSquares.add(move.getEndPosition());
      }
    }
    

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
        var hi = highlightedSquares.contains(pos);
        if ((row + col) % 2 == 0) {
          s += hi ? EscapeSequences.SET_BG_COLOR_GREEN : EscapeSequences.SET_BG_COLOR_DARK_GREEN;
        } else {
          s += hi ? EscapeSequences.SET_BG_COLOR_GREEN : EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
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

  public static String gameBoardString(GameData g, ChessGame.TeamColor perspective) {
    return gameBoardString(g, perspective, null);
  }

  public ChessPosition parsePos(String pos) {
    if (pos.length() != 2) {
      return null;
    }

    pos = pos.toLowerCase();
    var col = pos.charAt(0) - 'a' + 1;
    if (col < 1 || col > 8) {
      return null;
    }

    var row = pos.charAt(1) - '0';
    if (row < 1 || row > 8) {
      return null;
    }

    return new ChessPosition(row, col);
  }
  
}
