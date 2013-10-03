package org.smoothbuild.fs.base.exc;

import java.io.FileNotFoundException;

import org.smoothbuild.fs.base.Path;

/**
 * Thrown to indicate that the file exists but is a directory rather than a
 * regular file, does not exist but cannot be created, or cannot be opened for
 * any other reason.
 */
@SuppressWarnings("serial")
public class CannotCreateFileException extends FileSystemException {

  public CannotCreateFileException(Path path, FileNotFoundException e) {
    super(buildMessage(path));
  }

  private static String buildMessage(Path path) {
    return "File " + path + " cannot be created.";
  }
}
