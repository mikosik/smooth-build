package org.smoothbuild.task.err;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.InfoCodeMessage;

public class TaskCompletedInfo extends InfoCodeMessage {
  public TaskCompletedInfo(CallLocation callLocation) {
    super(callLocation.location(), createMessage(callLocation.name()));
  }

  private static String createMessage(Name name) {
    return createMessage(name, "DONE");
  }

  public static String createMessage(Name name, String status) {
    return padEnd("[" + name.simple() + "]", 10, ' ') + " " + status;
  }
}
