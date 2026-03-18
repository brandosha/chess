package client.views;

import client.repl.ReplView;

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
      case "l", "logout" -> close();
      case "h", "help" -> help();
      case "q", "quit" -> controller.stop();
      default -> console.printf("Unknown command \"%s\"\n", cmd);
    }
  }

  public void help() {
    String helpText = """

        [l]ogout              | Login to your account
        [h]elp                | Show this help message
        [q]uit                | Exit the app

      """;
    
    console.printf(helpText);
  }
  
}
