package org.smoothbuild.command.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

public class NothingToDoError extends Message {
  public NothingToDoError() {
    super(ERROR, "Specify at least one function to execute.");
  }
}
