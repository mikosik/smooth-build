package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class IllegalPathError extends Message {
  public IllegalPathError(String paramName, String message) {
    super(ERROR, "Param '" + paramName + "' has illegal value. " + message);
  }
}
