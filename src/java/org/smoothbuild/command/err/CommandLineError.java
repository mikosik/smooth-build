package org.smoothbuild.command.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class CommandLineError extends Message {
  public CommandLineError(String message) {
    super(ERROR, "Incorrect command line\n  " + message);
  }
}
