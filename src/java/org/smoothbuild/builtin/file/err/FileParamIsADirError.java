package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class FileParamIsADirError extends ErrorMessage {
  public FileParamIsADirError(String paramName, Path path) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a file but a directory.");
  }
}
