package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class NoSuchFileException extends FileSystemException {
  public NoSuchFileException(Path path) {
    super("File " + path + " doesn't exist.");
  }
}
