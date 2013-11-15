package org.smoothbuild.builtin.java.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.fs.base.Path;
import org.smoothbuild.message.base.Message;

public class DuplicatePathInJarError extends Message {
  public DuplicatePathInJarError(Path path) {
    super(ERROR, "Jar file contains two files with the same path = " + path);
  }
}
