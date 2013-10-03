package org.smoothbuild.builtin.compress.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.type.api.Path;

public class DuplicatePathInZipError extends Message {
  public DuplicatePathInZipError(Path path) {
    super(ERROR, "Zip file contains two files with the same path = " + path);
  }
}
