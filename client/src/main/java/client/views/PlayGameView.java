package client.views;

import chess.ChessGame;
import client.repl.ReplView;
import datamodel.GameData;

public class PlayGameView extends ReplView {
  
  private final String authToken;
  private final GameData game;
  private final ChessGame.TeamColor perspective;

  public PlayGameView(String authToken, GameData game, ChessGame.TeamColor perspective) {
    this.authToken = authToken;
    this.game = game;
    this.perspective = perspective;
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
      case "l", "leave" -> close();
      case "h", "help" -> help();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  public void draw() {
    console.printf("%s", ObserveGameView.drawBoardString(game, perspective));
  }

  public void help() {
    String helpText = """

        [d]raw                | Redraw the game board
        [h]elp                | Show this help message
        [l]eave               | Leave the game

      """;
    
    console.printf(helpText);
  }
}
