package org.smoothbuild.task.exec.err;

import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.message.message.MessageType.INFO;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeMessage;

public class TaskCompletedInfo extends CodeMessage {
  public TaskCompletedInfo(CallLocation callLocation) {
    super(INFO, callLocation.location(), createMessage(callLocation.name()));
  }

  private static String createMessage(String name) {
    return createMessage(name, "DONE");
  }

  public static String createMessage(String name, String status) {
    return padEnd("[" + name + "]", 10, ' ') + " " + status;
  }
}
