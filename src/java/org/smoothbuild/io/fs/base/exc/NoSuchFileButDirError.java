package org.smoothbuild.io.fs.base.exc;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchFileButDirError extends FileSystemError {
  public NoSuchFileButDirError(Path path) {
    super("File " + path + " doesn't exist. It is a directory.");
  }
}
