package org.smoothbuild.builtin.compress.err;

import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class DuplicatePathInZipException extends Exception {
  private final Path path;

  public DuplicatePathInZipException(Path path) {
    super("Zip file contains two files with the same path " + path);
    this.path = path;
  }

  public Path path() {
    return path;
  }
}
