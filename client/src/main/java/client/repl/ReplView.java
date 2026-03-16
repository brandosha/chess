package client.repl;

import java.io.Console;

abstract public class ReplView {
  public ReplController controller;
  public Console console;
  public Boolean closed = false;

  // Read, Eval, Print
  public abstract void rep();

  public void onAppear() {
  }

  public void close() {
    this.closed = true;
  }
}
