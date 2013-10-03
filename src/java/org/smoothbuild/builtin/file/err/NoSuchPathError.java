package org.smoothbuild.builtin.file.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class NoSuchPathError extends Message {
  public NoSuchPathError(String paramName, Path dirPath) {
    super(ERROR, "Param '" + paramName + "' has illegal value. Path " + dirPath + " does not exist");
  }
}
