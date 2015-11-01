package org.smoothbuild.io.fs.base.err;

import java.io.IOException;

/**
 * Indicates problem accessing a file. It is caused either by a bug in
 * smooth-build tool (or one of its plugins), IO problem of accessed file
 * system, unexpected change on accessed filesystem caused by some other
 * process.
 *
 * Plugin code should not catch this exception but let it propagate higher to
 * the caller (smooth build framework) which will correctly report it to user.
 */

public class FileSystemError extends RuntimeException {

  public FileSystemError(IOException e) {
    super("I/O operation failed", e);
  }

  public FileSystemError(String message) {
    super(message);
  }
}
