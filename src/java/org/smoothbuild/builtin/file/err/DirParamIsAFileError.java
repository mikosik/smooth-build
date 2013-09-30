package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class DirParamIsAFileError extends ErrorMessage {
  public DirParamIsAFileError(String paramName, Path path) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a directory but a file.");
  }
}
