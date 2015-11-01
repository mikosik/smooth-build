package org.smoothbuild.io.fs.base.err;

import org.smoothbuild.io.fs.base.Path;

public class NoSuchFileButDirException extends FileSystemException {
  public NoSuchFileButDirException(Path path) {
    super("File " + path + " doesn't exist. It is a directory.");
  }
}
