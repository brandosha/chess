package client.views;

import chess.ChessGame;
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
      case "l", "leave" -> close();
      case "h", "help" -> help();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  @Override
  public void draw() {
    console.printf("%s", gameBoardString(game, perspective));
  }

  @Override
  public void help() {
    String helpText = """

        [d]raw                | Redraw the game board
        [h]elp                | Show this help message
        [l]eave               | Leave the game

      """;
    
    console.printf(helpText);
  }
}
