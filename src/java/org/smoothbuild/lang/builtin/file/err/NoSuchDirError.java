package org.smoothbuild.lang.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class NoSuchDirError extends Message {
  public NoSuchDirError(Path path) {
    super(ERROR, "Dir " + path + " doesn't exist.");
  }
}
