package org.smoothbuild.task.err;

import static org.smoothbuild.command.SmoothContants.BUILD_DIR;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.Error;

import com.google.common.base.Throwables;

@SuppressWarnings("serial")
public class FileSystemError extends Error {
  public FileSystemError(Name name, Throwable e) {
    super(createMessage(name, e));
  }

  private static String createMessage(Name name, Throwable e) {
    StringBuilder b = new StringBuilder();

    b.append("Invoking function " + name + " caused internal exception:\n");
    b.append("It was probably caused by:\n");
    b.append(" - some other process (maybe other instance of Smooth) changing content of "
        + BUILD_DIR + " dir\n");
    b.append(" - bug in " + name + " function (plugin) implementation\n");
    b.append(" - bug in Smooth tool\n");
    b.append("Full java stacktrace below:\n");
    b.append(Throwables.getStackTraceAsString(e));

    return b.toString();
  }
}
