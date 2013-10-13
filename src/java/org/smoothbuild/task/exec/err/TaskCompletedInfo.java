package org.smoothbuild.task.exec.err;

import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.message.message.MessageType.INFO;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.CallLocation;

public class TaskCompletedInfo extends CodeMessage {
  public TaskCompletedInfo(CallLocation callLocation) {
    super(INFO, callLocation.location(), createMessage(callLocation.name()));
  }

  private static String createMessage(Name name) {
    return createMessage(name, "DONE");
  }

  public static String createMessage(Name name, String status) {
    return padEnd("[" + name.simple() + "]", 10, ' ') + " " + status;
  }
}
