package client.views;

import client.repl.ReplView;

public class PreloginView extends ReplView {

  @Override
  public void onAppear() {
    printHelp();
  }

  @Override
  public void rep() {
    var cmd = console.readLine("> ");
      switch (cmd) {
        case "h", "help" -> printHelp();
        case "q", "quit" -> close();
        default -> console.printf("Unknown command \"%s\"\n", cmd);
      }
  }

  public void printHelp() {
    String helpText = """

        [r]egister <username> | Create a new account with the given username
        [l]ogin <username>    | Login to your account
        [q]uit                | Exit the app
        [h]elp                | Show this help message

      """;
    
    console.printf(helpText);
  }
  
}
