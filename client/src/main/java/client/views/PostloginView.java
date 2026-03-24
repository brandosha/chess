package client.views;

import java.io.IOException;
import java.util.HashMap;

import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.ServerResponseException;
import datamodel.GameData;
import datamodel.http.CreateGameRequest;

public class PostloginView extends ReplView {

  private final String authToken;
  private HashMap<Integer, GameData> games = new HashMap<>();

  public PostloginView(String authToken) {
    this.authToken = authToken;
  }

  @Override
  public void onAppear() {
    help();
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
      case "c", "create" -> create(argv);
      case "s", "show" -> show();
      case "o", "observe" -> observe(argv);
      case "l", "logout" -> logout();
      case "h", "help" -> help();
      case "q", "quit" -> controller.stop();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  public void logout() {
    try {
      ServerFacade.local.logout(authToken);
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
    if (args.length != 2) {
      console.printf("Usage: [c]reate <name>");
    }
    
    try {
      var request = new CreateGameRequest(args[1]);
      var response = ServerFacade.local.createGame(request, authToken);
      console.printf("Game created\n  %d. %s\n", response.gameID, args[1]);
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

  public void show() {
    try {
      var response = ServerFacade.local.listGames(authToken);

      games.clear();
      for (GameData game : response.games) {
        games.put(game.gameID, game);
        console.printf("%d. %s\n", game.gameID, game.gameName);
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

  public void observe(String[] argv) {
    if (argv.length != 2) {
      console.printf("Usage: [o]bserve <game id>\n");
      return;
    }

    int id = -1;
    try {
      id = Integer.parseInt(argv[1]);
    } catch (NumberFormatException e) {
      console.printf("Invalid game id: %s is not a number\n", argv[1]);
      return;
    }

    var game = games.get(id);
    if (game == null) {
      console.printf("No game with id %s. Use 'show' to see a list of all games\n", argv[1]);
    } else {
      controller.push(new ObserveGameView(authToken, game));
    }
  }

  public void help() {
    String helpText = """

        [c]reate <name>       | Create a new game
        [s]how                | Show a list of all games
        [o]bserve <game id>   | Observe a game
        [l]ogout              | Log out of your current session
        [h]elp                | Show this help message
        [q]uit                | Exit the app

      """;
    
    console.printf(helpText);
  }
}
