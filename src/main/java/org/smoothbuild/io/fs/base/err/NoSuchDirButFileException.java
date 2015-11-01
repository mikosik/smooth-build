package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class NoSuchDirButFileException extends FileSystemException {
  public NoSuchDirButFileException(Path path) {
    super("Dir " + path + " doesn't exist. It is a file.");
  }
}
