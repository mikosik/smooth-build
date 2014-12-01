package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class PathIsAlreadyTakenByFileError extends FileSystemError {
  public PathIsAlreadyTakenByFileError(Path path) {
    super("Cannot use " + path + " path. It is already taken by file.");
  }
}
