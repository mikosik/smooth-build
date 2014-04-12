package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchFileError extends FileSystemError {
  public NoSuchFileError(Path path) {
    super("File " + path + " doesn't exist.");
  }
}
