package org.smoothbuild.io.fs.base.exc;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchPathException extends FileSystemException {
  public NoSuchPathException(Path path) {
    super("No such path: " + path);
  }
}
