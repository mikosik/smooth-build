package org.smoothbuild.task.err;

import org.smoothbuild.message.message.TaskLocation;
import org.smoothbuild.message.message.InfoCodeMessage;

public class TaskFailedError extends InfoCodeMessage {
  public TaskFailedError(TaskLocation taskLocation) {
    super(taskLocation.location(), createMessage(taskLocation));
  }

  private static String createMessage(TaskLocation taskLocation) {
    return TaskCompletedInfo.createMessage(taskLocation.name(), "FAILED");
  }
}
