package org.smoothbuild.fs.base.exc;

import org.smoothbuild.plugin.api.Path;

@SuppressWarnings("serial")
public class NoSuchFileException extends FileSystemException {
  public NoSuchFileException(Path path) {
    super(message(path));
  }

  private static String message(Path path) {
    return "Cannot find " + path.toString() + " file.";
  }
}
