package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class NoSuchDirException extends FileSystemException {
  public NoSuchDirException(Path path) {
    super("Dir " + path + " doesn't exists.");
  }
}
