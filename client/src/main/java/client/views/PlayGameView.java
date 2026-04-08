package client.views;

import chess.ChessGame;
import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.WebSocketFacade;
import datamodel.GameData;

public class PlayGameView extends ReplView {
  
  private final ServerFacade serverFacade;
  private WebSocketFacade wsFacade;
  private final String authToken;
  private final GameData game;
  private final ChessGame.TeamColor perspective;

  public PlayGameView(ServerFacade serverFacade, String authToken, GameData game, ChessGame.TeamColor perspective) {
    this.serverFacade = serverFacade;
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

  public void draw() {
    console.printf("%s", ObserveGameView.gameBoardString(game, perspective));
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
