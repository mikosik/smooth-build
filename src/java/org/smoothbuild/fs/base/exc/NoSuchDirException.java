package org.smoothbuild.fs.base.exc;

import org.smoothbuild.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchDirException extends FileSystemException {

  public NoSuchDirException(Path path) {
    super(message(path));
  }

  private static String message(Path path) {
    return "Path " + path + " is not a directory.";
  }
}
