package org.smoothbuild.io.fs.base.exc;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchDirButFileError extends FileSystemError {
  public NoSuchDirButFileError(Path path) {
    super("Dir " + path + " doesn't exist. It is a file.");
  }
}
