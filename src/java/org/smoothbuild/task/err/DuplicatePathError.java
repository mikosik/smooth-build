package org.smoothbuild.task.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class DuplicatePathError extends Message {
  public DuplicatePathError(Path path) {
    super(ERROR, "Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
