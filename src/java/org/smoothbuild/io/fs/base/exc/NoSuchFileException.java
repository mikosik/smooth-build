package org.smoothbuild.io.fs.base.exc;

import org.smoothbuild.io.fs.base.Path;

@SuppressWarnings("serial")
public class NoSuchFileException extends FileSystemException {
  public NoSuchFileException(Path path) {
    super("No such file: " + path);
  }
}
