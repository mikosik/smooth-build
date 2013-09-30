package org.smoothbuild.builtin.file.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class DirParamSubdirIsAFileError extends ErrorMessage {
  public DirParamSubdirIsAFileError(String paramName, Path path, Path subPath) {
    super("Param '" + paramName + "' has illegal value. Path " + path
        + " cannot be created because subpath " + subPath + " is not a directory but a file.");
  }
}
