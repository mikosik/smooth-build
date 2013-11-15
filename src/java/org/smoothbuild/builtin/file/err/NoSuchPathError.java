package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class NoSuchPathError extends Message {
  public NoSuchPathError(String paramName, Path dirPath) {
    super(ERROR, "Param '" + paramName + "' has illegal value. Path " + dirPath + " does not exist");
  }
}
