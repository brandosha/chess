package client.repl;

import java.io.Console;
import java.util.ArrayList;

public class ReplController {
  private final ArrayList<ReplView> stack = new ArrayList<>();
  private final Console console = System.console();
  private boolean stopped = false;

  public void start(ReplView startView) {
    if (console == null || !console.isTerminal()) {
      throw new RuntimeException("REPL can only by run in a command line environment");
    }

    this.stopped = false;
    this.push(startView);

    ReplView view = null;
    while (!stopped && !stack.isEmpty()) {
      var topView = this.stack.getLast();
      if (topView != view) {
        view = topView;
        view.onAppear();
      }

      if (view == null || view.closed) {
        this.stack.removeLast();
      } else {
        view.rep();
      }
    }
  }

  public void push(ReplView view) {
    this.stack.add(view);
    view.controller = this;
    view.console = console;
  }

  public void stop() {
    this.stopped = true;
  }
}
