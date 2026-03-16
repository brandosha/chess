package client.views;

import java.io.Console;

import client.repl.ReplView;

public class PregameView extends ReplView {

  @Override
  public void rep(Console console) {
    var cmd = console.readLine("> ");
    if (cmd == null || cmd.equals("x")) {
      this.close();
    }
    console.printf("\"%s\"\n", cmd);
  }
}
