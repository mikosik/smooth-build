package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class PathIsAlreadyTakenError extends FileSystemError {
  public PathIsAlreadyTakenError(Path path) {
    super("Cannot use " + path + " path. It is already taken.");
  }
}
