package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class DirParamSubdirIsAFileError extends Message {
  public DirParamSubdirIsAFileError(String paramName, Path path, Path subPath) {
    super(ERROR, "Param '" + paramName + "' has illegal value. Path " + path
        + " cannot be created because subpath " + subPath + " is not a directory but a file.");
  }
}
