package org.smoothbuild.io.fs.base.exc;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchPathError extends FileSystemError {
  public NoSuchPathError(Path path) {
    super("Path " + path + " doesn't exists.");
  }
}
