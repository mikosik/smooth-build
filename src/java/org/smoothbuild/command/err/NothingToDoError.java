package org.smoothbuild.command.err;

public class NothingToDoError extends CommandLineError {
  public NothingToDoError() {
    super("Specify at least one function to execute.");
  }
}
