package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class PathIsAlreadyTakenException extends FileSystemException {
  public PathIsAlreadyTakenException(Path path) {
    super("Cannot use " + path + " path. It is already taken.");
  }
}
