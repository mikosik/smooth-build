package org.smoothbuild.builtin.android.err;

import static org.smoothbuild.builtin.util.Exceptions.stackTraceToString;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.util.LineBuilder;

public class RunningAidlBinaryFailedError extends Message {
  public RunningAidlBinaryFailedError(List<String> command, IOException e) {
    super(MessageType.ERROR, createMessage(command, e));
  }

  private static String createMessage(List<String> command, IOException e) {
    LineBuilder b = new LineBuilder();

    b.addLine("Following command line failed:");
    b.addLine(join(command));
    b.addLine("stack trace is:");
    b.add(stackTraceToString(e));

    return b.build();
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
