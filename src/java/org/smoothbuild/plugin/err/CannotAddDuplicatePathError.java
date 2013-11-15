package org.smoothbuild.plugin.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class CannotAddDuplicatePathError extends Message {
  public CannotAddDuplicatePathError(Path path) {
    super(ERROR, "Error in function implementation: It tried to add two files with path = " + path
        + " to File*");
  }
}
