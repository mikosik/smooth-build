package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class PathIsAlreadyTakenByDirException extends FileSystemException {
  public PathIsAlreadyTakenByDirException(Path path) {
    super("Cannot use " + path + " path. It is already taken by dir.");
  }
}
