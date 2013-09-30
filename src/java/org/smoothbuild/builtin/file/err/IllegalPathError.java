package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;

public class IllegalPathError extends ErrorMessage {
  public IllegalPathError(String paramName, String message) {
    super("Param '" + paramName + "' has illegal value. " + message);
  }
}
