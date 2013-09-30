package org.smoothbuild.task.err;

import static org.smoothbuild.message.listen.MessageType.INFO;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.TaskLocation;

public class TaskFailedError extends CodeMessage {
  public TaskFailedError(TaskLocation taskLocation) {
    super(INFO, taskLocation.location(), createMessage(taskLocation));
  }

  private static String createMessage(TaskLocation taskLocation) {
    return TaskCompletedInfo.createMessage(taskLocation.name(), "FAILED");
  }
}
