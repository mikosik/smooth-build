package org.smoothbuild.io.fs.base.exc;

import static org.smoothbuild.io.IoConstants.SMOOTH_DIR;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.io.IOException;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.util.LineBuilder;

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
    LineBuilder b = new LineBuilder();

    b.addLine("Accessing file system caused error:");
    b.addLine("It was probably caused by:");
    b.addLine(" - some other process (maybe other instance of Smooth) changing content of "
        + SMOOTH_DIR + " dir");
    b.addLine(" - bug in plugin implementation");
    b.addLine(" - bug in Smooth tool or one of builtin functions");
    b.addLine("Full java stacktrace below:");
    b.add(Throwables.getStackTraceAsString(e));

    return b.build();
  }
}
