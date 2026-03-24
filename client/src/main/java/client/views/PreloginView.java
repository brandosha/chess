package client.views;

import java.io.IOException;

import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.ServerResponseException;
import datamodel.http.LoginRequest;
import datamodel.http.RegisterRequest;

public class PreloginView extends ReplView {

  private final ServerFacade serverFacade;

  public PreloginView(ServerFacade serverFacade) {
    this.serverFacade = serverFacade;
  }

  @Override
  public void onAppear() {
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
      case "r", "register" -> register(argv);
      case "l", "login" -> login(argv);
      case "h", "help" -> help();
      case "q", "quit" -> controller.stop();
      default -> console.printf("Unknown command \"%s\"\n", argv[0]);
    }
  }

  public void register(String[] argv) {
    if (argv.length != 2) {
      console.printf("Usage: [r]egister <username>\n");
      return;
    }

    var password = new String(console.readPassword("Password: "));

    try {
      var request = new RegisterRequest(argv[1], password, "");
      var response = serverFacade.register(request);
      
      controller.push(new PostloginView(serverFacade, response.authToken, response.username));
    } catch (ServerResponseException | IOException | InterruptedException e) {
      console.printf("Registration failed: %s\n", e.getMessage());
    }
  }

  public void login(String[] argv) {
    if (argv.length != 2) {
      console.printf("Usage: [l]ogin <username>\n");
      return;
    }

    var password = new String(console.readPassword("Password: "));
    try {
      var request = new LoginRequest(argv[1], password);
      var response = serverFacade.login(request);
      
      controller.push(new PostloginView(serverFacade, response.authToken, response.username));
    } catch (ServerResponseException | IOException | InterruptedException e) {
      console.printf("Login failed: %s\n", e.getMessage());
    }
  }

  public void help() {
    String helpText = """

        [r]egister <username> | Create a new account with the given username
        [l]ogin <username>    | Login to your account
        [h]elp                | Show this help message
        [q]uit                | Exit the app

      """;
    
    console.printf(helpText);
  }

}
