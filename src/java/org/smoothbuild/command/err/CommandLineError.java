package org.smoothbuild.command.err;

import org.smoothbuild.message.message.ErrorMessage;

public class CommandLineError extends ErrorMessage {
  public CommandLineError(String message) {
    super("Incorrect command line\n  " + message);
  }
}
