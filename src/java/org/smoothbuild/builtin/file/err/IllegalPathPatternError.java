package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

public class IllegalPathPatternError extends Message {
  public IllegalPathPatternError(String paramName, String message) {
    super(ERROR, "Parameter '" + paramName + "' has illegal value. " + message);
  }
}
