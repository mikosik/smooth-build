package org.smoothbuild.builtin.java.err;

import static org.smoothbuild.lang.message.MessageType.ERROR;

import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.message.Message;

public class DuplicatePathInJarError extends Message {
  public DuplicatePathInJarError(Path path) {
    super(ERROR, "Jar file contains two files with the same path = " + path);
  }
}
