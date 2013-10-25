package org.smoothbuild.fs.base.exc;

import org.smoothbuild.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchFileException extends FileSystemException {
  public NoSuchFileException(Path path) {
    super("Path " + path + " is not a file.");
  }
}
