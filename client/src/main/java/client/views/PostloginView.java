package client.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import chess.ChessGame;
import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.ServerResponseException;
import datamodel.GameData;
import datamodel.http.CreateGameRequest;
import datamodel.http.JoinGameRequest;

public class PostloginView extends ReplView {

  private final ServerFacade serverFacade;
  private final String authToken;
  private final String username;
  private final HashMap<Integer, GameData> games = new HashMap<>();

  public PostloginView(ServerFacade serverFacade, String authToken, String username) {
    this.serverFacade = serverFacade;
    this.authToken = authToken;
    this.username = username;
  }

  @Override
  public void onAppear() {
    console.printf("\n\nGames:\n");
    list();
    help();
  }

  @Override
  public void rep() {
    var argv = readCmd("> ");
    if (argv == null) {
      controller.stop();
      return;
    }

    switch (argv[0]) {
      case "c", "create" -> create(argv);
      case "l", "list" -> list();
      case "j", "join" -> join(argv);
      case "o", "observe" -> observe(argv);
      case "g", "logout" -> logout();
      case "h", "help" -> help();
      case "q", "quit" -> controller.stop();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  public void logout() {
    try {
      serverFacade.logout(authToken);
      close();
    } catch (IOException | InterruptedException e) {
      console.printf("Logout failed: %s\n", e.getMessage());
    } catch (ServerResponseException e) {
      if (e.response.statusCode() == 401) {
        close();
      } else {
        console.printf("Logout failed: %s\n", e.getMessage());
      }
    }
  }

  public void create(String[] args) {
    if (args.length < 2) {
      console.printf("Usage: [c]reate <name>");
    }

    String name = args[1];
    for (int i = 2; i < args.length; i++) {
      name += " " + args[i];
    }
    
    try {
      var request = new CreateGameRequest(name);
      // var response = serverFacade.createGame(request, authToken);
      // console.printf("Game created\n  %d. %s\n", response.gameID, args[1]);
      serverFacade.createGame(request, authToken);
      console.printf("Game created\n");
      list();
    } catch (IOException | InterruptedException e) {
      console.printf("Failed to create game: %s\n", e.getMessage());
    } catch (ServerResponseException e) {
      if (e.response.statusCode() == 401) {
        console.printf("Session has ended, logging out.\n");
        close();
      } else {
        console.printf("Failed to create game: %s\n", e.getMessage());
      }
    }
  }

  public void list() {
    try {
      var response = serverFacade.listGames(authToken);

      games.clear();
      int i = 1;
      for (GameData game : response.games) {
        games.put(i, game);
        console.printf("%d. %s\n", i, game.gameName);
        console.printf(" - Black: %s\n", game.blackUsername == null ? "--" : game.blackUsername);
        console.printf(" - White: %s\n\n", game.whiteUsername == null ? "--" : game.whiteUsername);
        i += 1;
      }
    } catch (IOException | InterruptedException e) {
      console.printf("Failed to get games: %s\n", e.getMessage());
    } catch (ServerResponseException e) {
      if (e.response.statusCode() == 401) {
        console.printf("Session has ended, logging out.\n");
        close();
      } else {
        console.printf("Failed to get games: %s\n", e.getMessage());
      }
    }
  }

  public void join(String[] argv) {
    if (argv.length != 2) {
      console.printf("Usage: [j]oin <game id>\n");
      return;
    }

    var game = getGame(argv[1]);
    if (game == null) { return; }

    ArrayList<String> availableColors = new ArrayList<>();

    String players = "Players:\n";
    if (game.blackUsername == null) {
      players += " - Black: --\n";
      availableColors.add("b");
    } else {
      players += " - Black: " + game.blackUsername + "\n";
    }

    if (game.whiteUsername == null) {
      players += " - White: --";
      availableColors.add("w");
    } else {
      players += " - White: " + game.whiteUsername;
    }

    console.printf("%s\n", players);

    var color = console.readLine("\nChoose your color (%s): ", String.join("/", availableColors));
    if (!availableColors.contains(color)) {
      console.printf("%s is not a valid option\n", color);
      return;
    }

    ChessGame.TeamColor team = switch (color) {
      case "b" -> ChessGame.TeamColor.BLACK;
      case "w" -> ChessGame.TeamColor.WHITE;
      default -> null;
    };

    color = switch (color) {
      case "b" -> "BLACK";
      case "w" -> "WHITE";
      default -> null;
    };

    try {
      var request = new JoinGameRequest(color, game.gameID);
      serverFacade.joinGame(request, authToken);

      switch (team) {
        case BLACK -> game.blackUsername = username;
        case WHITE -> game.whiteUsername = username;
      }
      controller.push(new PlayGameView(authToken, game, team));
    } catch (IOException | InterruptedException e) {
      console.printf("Failed to join game: %s\n", e.getMessage());
    } catch (ServerResponseException e) {
      if (e.response.statusCode() == 401) {
        console.printf("Session has ended, logging out.\n");
        close();
      } else {
        console.printf("Failed to join game: %s\n", e.getMessage());
      }
    }
  }

  public void observe(String[] argv) {
    if (argv.length != 2) {
      console.printf("Usage: [o]bserve <game id>\n");
      return;
    }

    var game = getGame(argv[1]);
    if (game != null) {
      controller.push(new ObserveGameView(authToken, game));
    }
  }

  public void help() {
    String helpText = """

        [c]reate <name>       | Create a new game
        [l]ist                | Show a list of all games
        [j]oin <game id>      | Join a game
        [o]bserve <game id>   | Observe a game
        lo[g]out              | Log out of your current session
        [h]elp                | Show this help message
        [q]uit                | Exit the app

      """;
    
    console.printf(helpText);
  }

  private GameData getGame(String idStr) {
    int id;
    try {
      id = Integer.parseInt(idStr);
    } catch (NumberFormatException e) {
      console.printf("Invalid game id: %s is not a number\n", idStr);
      return null;
    }

    var game = games.get(id);
    if (game == null) {
      console.printf("No game with id %s. Use 'list' to see a list of all games\n", idStr);
    }

    return game;
  }
}
