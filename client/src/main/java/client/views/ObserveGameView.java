package client.views;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import client.repl.ReplView;
import datamodel.GameData;
import ui.EscapeSequences;

public class ObserveGameView extends ReplView {

  private final String authToken;
  private final GameData game;

  public ObserveGameView(String authToken, GameData game) {
    this.authToken = authToken;
    this.game = game;
  }

  @Override
  public void onAppear() {
    help();
    draw();
  }

  @Override
  public void rep() {
    var cmd = console.readLine("> ");
    if (cmd == null) {
      controller.stop();
      return;
    }
    var argv = cmd.split("\\s+");

    switch (argv[0]) {
      case "d", "draw" -> draw();
      case "s", "stop" -> close();
      case "h", "help" -> help();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  public void draw() {
    console.printf("%s", drawBoardString(game, ChessGame.TeamColor.WHITE));
  }

  public void help() {
    String helpText = """

        [d]raw                | Redraw the game board
        [h]elp                | Show this help message
        [s]top                | Stop observing

      """;
    
    console.printf(helpText);
  }

  public static String drawBoardString(GameData g, ChessGame.TeamColor perspective) {
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
