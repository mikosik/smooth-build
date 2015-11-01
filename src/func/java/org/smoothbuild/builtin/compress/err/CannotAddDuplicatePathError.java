package org.smoothbuild.builtin.compress.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.Message;

public class CannotAddDuplicatePathError extends Message {
  public CannotAddDuplicatePathError(Path path) {
    super(ERROR, "Cannot zip two files with the same path = " + path);
  }
}
