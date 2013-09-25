package org.smoothbuild.fs.base.exc;

import org.smoothbuild.plugin.api.Path;
import org.smoothbuild.task.err.FileSystemError;

@SuppressWarnings("serial")
public class NoSuchFileException extends FileSystemError {
  public NoSuchFileException(Path path) {
    super(message(path));
  }

  private static String message(Path path) {
    return "Cannot find " + path.toString() + " file.";
  }
}
