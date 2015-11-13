package org.smoothbuild.builtin.java.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.lang.message.Message;

public class CannotAddDuplicatePathError extends Message {
  public CannotAddDuplicatePathError(String path) {
    super(ERROR, "Cannot jar two files with the same path = " + path);
  }
}
