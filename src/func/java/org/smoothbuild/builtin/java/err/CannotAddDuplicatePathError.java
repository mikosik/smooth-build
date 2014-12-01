package org.smoothbuild.builtin.java.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class CannotAddDuplicatePathError extends Message {
  public CannotAddDuplicatePathError(Path path) {
    super(ERROR, "Cannot jar two files with the same path = " + path);
  }
}
