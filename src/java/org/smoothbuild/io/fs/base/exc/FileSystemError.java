package org.smoothbuild.io.fs.base.exc;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.io.IOException;

import org.smoothbuild.message.base.Message;

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
public class FileSystemError extends Message {

  public FileSystemError(IOException e) {
    super(ERROR, "I/O operation failed", e);
  }

  public FileSystemError(String message) {
    super(ERROR, message);
  }
}
