package client.repl;

import java.io.Console;
import java.util.ArrayList;

public class ReplController {
  private final ArrayList<ReplView> stack = new ArrayList<>();
  private final Console console = System.console();

  public void start(ReplView startView) {
    if (console == null) {
      throw new RuntimeException("REPL can only by run in a command line environment");
    }

    this.push(startView);

    while (!stack.isEmpty()) {
      var view = this.stack.getLast();
      if (view.closed) {
        this.stack.removeLast();
      } else {
        view.rep(console);
      }
    }
  }

  public void push(ReplView view) {
    this.stack.add(view);
    view.controller = this;
  }
}
