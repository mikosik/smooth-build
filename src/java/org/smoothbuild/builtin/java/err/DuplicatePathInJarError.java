package org.smoothbuild.builtin.java.err;

import org.smoothbuild.message.message.Error;
import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class DuplicatePathInJarError extends Error {
  public DuplicatePathInJarError(Path path) {
    super("Jar file contains two files with the same path = " + path);
  }
}
