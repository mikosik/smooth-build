package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class IllegalPathForFileError extends FileSystemError {
  public IllegalPathForFileError(Path path) {
    super("Cannot create file at " + path + " path. It points to a directory.");
  }
}
