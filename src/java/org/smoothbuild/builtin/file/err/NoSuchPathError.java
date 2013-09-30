package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class NoSuchPathError extends ErrorMessage {
  public NoSuchPathError(String paramName, Path dirPath) {
    super("Param '" + paramName + "' has illegal value. Path " + dirPath + " does not exist");
  }
}
