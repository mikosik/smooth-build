package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchDirError extends FileSystemError {
  public NoSuchDirError(Path path) {
    super("Dir " + path + " doesn't exists.");
  }
}
