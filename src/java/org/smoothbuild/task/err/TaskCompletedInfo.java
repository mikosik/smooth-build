package org.smoothbuild.task.err;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.TaskLocation;
import org.smoothbuild.message.message.InfoCodeMessage;

public class TaskCompletedInfo extends InfoCodeMessage {
  public TaskCompletedInfo(TaskLocation taskLocation) {
    super(taskLocation.location(), createMessage(taskLocation.name()));
  }

  private static String createMessage(Name name) {
    return createMessage(name, "DONE");
  }

  public static String createMessage(Name name, String status) {
    return padEnd("[" + name.simple() + "]", 10, ' ') + " " + status;
  }
}
