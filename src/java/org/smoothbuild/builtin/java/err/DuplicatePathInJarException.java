package org.smoothbuild.builtin.java.err;

import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class DuplicatePathInJarException extends Exception {
  private final Path path;

  public DuplicatePathInJarException(Path path) {
    super("Jar file contains two files with the same path " + path);
    this.path = path;
  }

  public Path path() {
    return path;
  }
}
