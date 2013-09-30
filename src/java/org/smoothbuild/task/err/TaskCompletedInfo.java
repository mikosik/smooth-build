package org.smoothbuild.task.err;

import static com.google.common.base.Strings.padEnd;
import static org.smoothbuild.message.listen.MessageType.INFO;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.TaskLocation;

public class TaskCompletedInfo extends CodeMessage {
  public TaskCompletedInfo(TaskLocation taskLocation) {
    super(INFO, taskLocation.location(), createMessage(taskLocation.name()));
  }

  private static String createMessage(Name name) {
    return createMessage(name, "DONE");
  }

  public static String createMessage(Name name, String status) {
    return padEnd("[" + name.simple() + "]", 10, ' ') + " " + status;
  }
}
