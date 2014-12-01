package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class NoSuchFileButDirError extends FileSystemError {
  public NoSuchFileButDirError(Path path) {
    super("File " + path + " doesn't exist. It is a directory.");
  }
}
