package client.views;

import java.io.IOException;

import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.ServerResponseException;
import datamodel.http.CreateGameRequest;

public class PostloginView extends ReplView {

  private final String authToken;

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

  public void help() {
    String helpText = """

        [c]reate <name>       | Create a new game
        [l]ogout              | Log out of your current session
        [h]elp                | Show this help message
        [q]uit                | Exit the app

      """;
    
    console.printf(helpText);
  }
  
}
