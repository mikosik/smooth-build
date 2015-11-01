package org.smoothbuild.builtin.compress.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.Message;

public class DuplicatePathInZipError extends Message {
  public DuplicatePathInZipError(Path path) {
    super(ERROR, "Zip file contains two files with the same path = " + path);
  }
}
