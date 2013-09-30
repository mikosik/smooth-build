package org.smoothbuild.fs.base.exc;

import java.io.IOException;

import org.smoothbuild.message.message.ErrorMessageException;

/**
 * Indicates problem accessing a file. It is caused either by a bug in
 * smooth-build tool (or one of its plugins), IO problem of accessed file
 * system, unexpected change on accessed filesystem caused by some other
 * process.
 * 
 * Plugin code should not catch this exception but let it propagate higher to
 * the caller (smooth build framework) which will correctly report it to user.
 */
@SuppressWarnings("serial")
public class FileSystemException extends ErrorMessageException {

  public FileSystemException(String message) {
    super(new FileSystemError(message));
  }

  public FileSystemException(IOException e) {
    super(new FileSystemError(e));
  }

  public FileSystemException(String string, IOException e) {
    super(new FileSystemError(string, e));
  }
}
