package client.views;

import java.io.IOException;

import client.repl.ReplView;
import client.server.ServerFacade;
import client.server.ServerResponseException;
import datamodel.http.RegisterRequest;

public class PreloginView extends ReplView {

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
      case "r", "register" -> register(argv);
      case "l", "login" -> login(argv);
      case "h", "help" -> help();
      case "q", "quit" -> controller.stop();
      default -> console.printf("Unknown command \"%s\"\n", cmd);
    }
  }

  public void register(String[] argv) {
    var password = new String(console.readPassword("Password: "));
    console.printf("Registering with %s => %s\n", argv[1], password);

    try {
      var request = new RegisterRequest(argv[1], password, "");
      var response = ServerFacade.local.register(request);
      System.out.println(response);
    } catch (ServerResponseException | IOException | InterruptedException e) {
      console.printf("Registration failed: %s", e.getMessage());
    }
  }

  public void login(String[] argv) {
    var password = new String(console.readPassword("Password: "));
    console.printf("Logging in with %s => %s\n", argv[1], password);
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
