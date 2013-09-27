package org.smoothbuild.task.err;

import org.smoothbuild.message.message.Info;

@SuppressWarnings("serial")
public class TaskFailedError extends Info {
  public TaskFailedError(String name) {
    super(createMessage(name));
  }

  private static String createMessage(String name) {
    return TaskCompletedInfo.createMessage(name, "FAILED");
  }
}
