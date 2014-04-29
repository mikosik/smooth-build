package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class IllegalPathError extends Message {
  public IllegalPathError(String paramName, String message) {
    super(ERROR, "Param '" + paramName + "' has illegal value. " + message);
  }
}
