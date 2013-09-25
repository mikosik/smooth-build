package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.Error;

@SuppressWarnings("serial")
public class IllegalPathError extends Error {
  public IllegalPathError(String paramName, String message) {
    super("Param '" + paramName + "' has illegal value. " + message);
  }
}
