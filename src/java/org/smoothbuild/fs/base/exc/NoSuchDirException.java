package org.smoothbuild.fs.base.exc;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.task.err.FileSystemError;

@SuppressWarnings("serial")
public class NoSuchDirException extends FileSystemError {

  public NoSuchDirException(Path path) {
    super(message(path));
  }

  private static String message(Path path) {
    return "Path " + path + " is not a directory.";
  }
}
