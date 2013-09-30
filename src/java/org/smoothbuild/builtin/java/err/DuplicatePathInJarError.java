package org.smoothbuild.builtin.java.err;

import org.smoothbuild.message.message.ErrorMessage;
import org.smoothbuild.plugin.api.Path;

public class DuplicatePathInJarError extends ErrorMessage {
  public DuplicatePathInJarError(Path path) {
    super("Jar file contains two files with the same path = " + path);
  }
}
