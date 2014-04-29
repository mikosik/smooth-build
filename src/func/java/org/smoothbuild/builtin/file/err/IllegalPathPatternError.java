package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class IllegalPathPatternError extends Message {
  public IllegalPathPatternError(String paramName, String message) {
    super(ERROR, "Parameter '" + paramName + "' has illegal value. " + message);
  }
}
