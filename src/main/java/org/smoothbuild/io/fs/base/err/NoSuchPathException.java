package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class NoSuchPathException extends FileSystemException {
  public NoSuchPathException(Path path) {
    super("Path " + path + " doesn't exists.");
  }
}
