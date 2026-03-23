package client.views;

import java.io.IOException;

import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.ServerResponseException;

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
      case "l", "logout" -> logout();
      case "h", "help" -> help();
      case "q", "quit" -> controller.stop();
      default -> console.printf("Unknown command \"%s\"\n", cmd);
    }
  }

  public void logout() {
    try {
      ServerFacade.local.logout();
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

  public void help() {
    String helpText = """

        [l]ogout              | Log out of your current session
        [h]elp                | Show this help message
        [q]uit                | Exit the app

      """;
    
    console.printf(helpText);
  }
  
}
