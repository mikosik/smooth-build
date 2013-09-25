package org.smoothbuild.task.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import java.io.IOException;

import org.smoothbuild.message.Error;

import com.google.common.base.Throwables;

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
public class FileSystemError extends Error {
  public FileSystemError(Throwable e) {
    this(createMessage(e));
  }

  public FileSystemError(String message) {
    super(message);
  }

  public FileSystemError(String message, IOException e) {
    super(message + "\nFull java stacktrace below:\n" + Throwables.getStackTraceAsString(e));
  }

  private static String createMessage(Throwable e) {
    StringBuilder b = new StringBuilder();

    b.append("Accessing file system caused error:\n");
    b.append("It was probably caused by:\n");
    b.append(" - some other process (maybe other instance of Smooth) changing content of "
        + BUILD_DIR + " dir\n");
    b.append(" - bug in function (plugin) implementation\n");
    b.append(" - bug in Smooth tool\n");
    b.append("Full java stacktrace below:\n");
    b.append(Throwables.getStackTraceAsString(e));

    return b.toString();
  }
}
