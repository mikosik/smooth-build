package org.smoothbuild.builtin.java.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.Message;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathInJarError extends Message {
  public DuplicatePathInJarError(Path path) {
    super(ERROR, "Jar file contains two files with the same path = " + path);
  }
}
