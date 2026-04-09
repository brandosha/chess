package client.views;

import java.io.IOException;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece.PieceType;
import client.server.ServerFacade;
import datamodel.GameData;

public class PlayGameView extends ObserveGameView {
  
  private final ChessGame.TeamColor perspective;

  public PlayGameView(ServerFacade serverFacade, String authToken, GameData game, ChessGame.TeamColor perspective) {
    super(serverFacade, authToken, game);
    this.perspective = perspective;
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
      case "m", "move" -> move(argv);
      case "r", "resign" -> resign();
      case "l", "leave" -> leave();
      case "h", "help" -> help();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  @Override
  public void draw() {
    System.out.printf("%s\n", gameBoardString(game, perspective));
  }

  public void move(String[] argv) {
    if (argv.length != 2) {
      console.printf("Usage: %s <move>\n", argv[0]);
      return;
    }

    var moveStr = argv[1];
    var start = parsePos(moveStr.substring(0, 2));
    var end = parsePos(moveStr.substring(2));

    if (start == null || end == null) {
      console.printf("'%s' is not a valid move\n", moveStr);
      return;
    }

    

    var board = game.game.getBoard();
    var movingPiece = board.getPiece(start);
    if (movingPiece == null) {
      console.printf("No piece at %s", start);
      return;
    }

    PieceType promotionPiece = null;

    if (movingPiece.getPieceType() == PieceType.PAWN) {
      // Check if promotion
      Boolean willPromote = false;
      var moves = game.game.validMoves(start);
      for (ChessMove m : moves) {
        if (m.getPromotionPiece() != null && m.getEndPosition().equals(end)) {
          willPromote = true;
          break;
        }
      }

      if (willPromote) {
        String prompt = """

              Select a piece to promote to:
              - [q]ueen
              - [r]ook
              - [b]ishop
              - k[n]ight
            
            """;
        var promotion = console.readLine("%s> ", prompt);
        switch (promotion) {
          case "q", "queen" -> promotionPiece = PieceType.QUEEN;
          case "r", "roook" -> promotionPiece = PieceType.ROOK;
          case "b", "bishop" -> promotionPiece = PieceType.BISHOP;
          case "n", "knight" -> promotionPiece = PieceType.KNIGHT;
          default -> {
            console.printf("'%s' is not a valid promotion piece\n", promotion);
          }
        }
      }
    }

    try {
      var move = new ChessMove(start, end, promotionPiece);
      wsFacade.makeMove(game.gameID, authToken, move);
    } catch (IOException e) {
      console.printf("Failed to send the move: %s\n", e.getMessage());
    }
  }

  public void resign() {
    var confirm = console.readLine("Are you sure you want to resign? (yes/no) ");
    if (confirm.equals("yes")) {
      try {
        wsFacade.resign(game.gameID, authToken);
        Thread.sleep(300);
        close();
      } catch (Exception e) {
        console.printf("Failed to resign:\n%s\n", e.getMessage());
      }
    }
  }

  @Override
  public void help() {
    String helpText = """

        [d]raw                | Redraw the game board
        h[i]ghlight <square>  | Highlight moves for a piece at a certain square (ex. e2)
        [m]ove <move>         | Make a move from one square to another (ex. e2e4)
        [r]esign              | Resign from the game
        [h]elp                | Show this help message
        [l]eave               | Leave the game

      """;
    
    console.printf(helpText);
  }
}
