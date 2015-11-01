package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.lang.message.Message;

public class IllegalPathError extends Message {
  public IllegalPathError(String paramName, String message) {
    super(ERROR, "Param '" + paramName + "' has illegal value. " + message);
  }
}
