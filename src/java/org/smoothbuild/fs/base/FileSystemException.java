package org.smoothbuild.fs.base;

import java.io.IOException;

/**
 * Thrown to indicate problem accessing a file. It is caused either by a bug in
 * smooth-build tool (or one of its plugins), IO problem of accessed file
 * system, unexpected change on accessed filesystem caused by some other
 * process.
 * 
 * Plugin code should not catch this exception but let it propagate higher to
 * the caller (smooth build framework) which will correctly report it to user.
 */
@SuppressWarnings("serial")
public class FileSystemException extends RuntimeException {

  public FileSystemException(String message) {
    super(message);
  }

  public FileSystemException(String message, Throwable e) {
    super(message, e);
  }

  public FileSystemException(IOException e) {
    super(e);
  }
}
