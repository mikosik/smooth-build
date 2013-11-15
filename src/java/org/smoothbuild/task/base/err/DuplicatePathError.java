package org.smoothbuild.task.base.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class DuplicatePathError extends Message {
  public DuplicatePathError(Path path) {
    super(ERROR, "Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
