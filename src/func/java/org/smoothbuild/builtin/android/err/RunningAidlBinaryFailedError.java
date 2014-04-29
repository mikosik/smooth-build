package org.smoothbuild.builtin.android.err;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.util.LineBuilder;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;

@SuppressWarnings("serial")
public class RunningAidlBinaryFailedError extends Message {
  public RunningAidlBinaryFailedError(List<String> command, IOException e) {
    super(MessageType.ERROR, createMessage(command, e));
  }

  private static String createMessage(List<String> command, IOException e) {
    LineBuilder b = new LineBuilder();

    b.addLine("Following command line failed:");
    b.addLine(Joiner.on(' ').join(command));
    b.addLine("stack trace is:");
    b.add(Throwables.getStackTraceAsString(e));

    return b.build();
  }
}
