package org.smoothbuild.fs.base.exc;

import java.io.FileNotFoundException;

import org.smoothbuild.plugin.api.Path;

/**
 * Thrown to indicate that the file exists but is a directory rather than a
 * regular file, does not exist but cannot be created, or cannot be opened for
 * any other reason.
 */
@SuppressWarnings("serial")
public class CannotCreateFileException extends FileSystemException {

  public CannotCreateFileException(Path path, FileNotFoundException e) {
    super(message(path), e);
  }

  private static String message(Path path) {
    return "File " + path + " cannot be created.";
  }
}
