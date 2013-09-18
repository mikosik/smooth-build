package org.smoothbuild.builtin.compress;

import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class DuplicatePathException extends Exception {
  private final Path path;

  public DuplicatePathException(Path path) {
    super("Zip file contains two files with the same path " + path);
    this.path = path;
  }

  public Path path() {
    return path;
  }
}
