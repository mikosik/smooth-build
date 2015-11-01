package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.builtin.util.Exceptions.stackTraceToString;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.MessageType;

public class RunningAidlBinaryFailedError extends Message {
  public RunningAidlBinaryFailedError(List<String> command, IOException e) {
    super(MessageType.ERROR, createMessage(command, e));
  }

  private static String createMessage(List<String> command, IOException e) {
    StringBuilder builder = new StringBuilder();
    builder.append("Following command line failed:\n");
    builder.append(join(command) + "\n");
    builder.append("stack trace is:\n");
    builder.append(stackTraceToString(e));
    return builder.toString();
  }

  private static String join(List<String> command) {
    StringBuilder result = new StringBuilder();
    String delimiter = "";
    for (String string : command) {
      result.append(delimiter);
      result.append(string);
      delimiter = " ";
    }
    return result.toString();
  }
}
