package org.smoothbuild.fs.base.exc;

import static org.smoothbuild.fs.FileSystemModule.SMOOTH_DIR;
import static org.smoothbuild.message.message.MessageType.ERROR;

import java.io.IOException;

import org.smoothbuild.message.message.Message;

import com.google.common.base.Throwables;

public class FileSystemError extends Message {
  public FileSystemError(Throwable e) {
    this(createMessage(e));
  }

  public FileSystemError(String message) {
    super(ERROR, message);
  }

  public FileSystemError(String message, IOException e) {
    super(ERROR, message + "\nFull java stacktrace below:\n" + Throwables.getStackTraceAsString(e));
  }

  private static String createMessage(Throwable e) {
    StringBuilder b = new StringBuilder();

    b.append("Accessing file system caused error:\n");
    b.append("It was probably caused by:\n");
    b.append(" - some other process (maybe other instance of Smooth) changing content of "
        + SMOOTH_DIR + " dir\n");
    b.append(" - bug in plugin implementation\n");
    b.append(" - bug in Smooth tool or one of builtin functions\n");
    b.append("Full java stacktrace below:\n");
    b.append(Throwables.getStackTraceAsString(e));

    return b.toString();
  }
}
