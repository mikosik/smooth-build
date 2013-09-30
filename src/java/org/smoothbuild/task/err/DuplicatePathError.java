package org.smoothbuild.task.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathError extends Message {
  public DuplicatePathError(Path path) {
    super(ERROR, "Two Files with the same path = " + path + " cannot belong to the same File*");
  }
}
