package org.smoothbuild.task.base.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class NullResultError extends Message {
  public NullResultError() {
    super(ERROR, "Faulty function implementation : 'null' was returned but no error reported.");
  }
}
