package org.smoothbuild.command.err;

import org.smoothbuild.message.Error;

@SuppressWarnings("serial")
public class CommandLineError extends Error {
  public CommandLineError(String message) {
    super("Incorrect command line\n  " + message);
  }
}
