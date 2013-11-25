package org.smoothbuild.lang.builtin.file.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class NoSuchFileError extends Message {
  public NoSuchFileError(Path path) {
    super(ERROR, "File " + path + " doesn't exist.");
  }
}
