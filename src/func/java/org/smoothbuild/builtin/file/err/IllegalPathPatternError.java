package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.lang.message.Message;

public class IllegalPathPatternError extends Message {
  public IllegalPathPatternError(String paramName, String message) {
    super(ERROR, "Parameter '" + paramName + "' has illegal value. " + message);
  }
}
