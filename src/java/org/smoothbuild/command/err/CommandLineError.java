package org.smoothbuild.command.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class CommandLineError extends Message {
  public CommandLineError(String message) {
    super(ERROR, "Incorrect command line\n" + message);
  }
}
