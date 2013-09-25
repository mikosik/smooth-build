package org.smoothbuild.command.err;

@SuppressWarnings("serial")
public class NothingToDoError extends CommandLineError {
  public NothingToDoError() {
    super("Specify at least one function to execute.");
  }
}
