package org.smoothbuild.task.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.type.api.Path;

public class DuplicatePathError extends Message {
  public DuplicatePathError(Path path) {
    super(ERROR, "Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
