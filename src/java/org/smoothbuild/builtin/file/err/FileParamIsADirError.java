package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class FileParamIsADirError extends Message {
  public FileParamIsADirError(String paramName, Path path) {
    super(ERROR, "Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a file but a directory.");
  }
}
