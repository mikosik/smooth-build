package org.smoothbuild.builtin.compress.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class DuplicatePathInZipError extends Message {
  public DuplicatePathInZipError(Path path) {
    super(ERROR, "Zip file contains two files with the same path = " + path);
  }
}
