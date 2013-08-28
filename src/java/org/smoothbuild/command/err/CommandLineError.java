package org.smoothbuild.command.err;

import org.smoothbuild.problem.Error;

public class CommandLineError extends Error {
  public CommandLineError(String message) {
    super(null, "Incorrect command line:\n" + message);
  }
}
