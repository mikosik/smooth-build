package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.type.api.Path;

public class DirParamIsAFileError extends Message {
  public DirParamIsAFileError(String paramName, Path path) {
    super(ERROR, "Param '" + paramName + "' has illegal value. Path " + path
        + " exists but is not a directory but a file.");
  }
}
