package org.smoothbuild.builtin.compress.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.message.Message;

public class DuplicatePathInZipError extends Message {
  public DuplicatePathInZipError(Path path) {
    super(ERROR, "Zip file contains two files with the same path = " + path);
  }
}
