package org.smoothbuild.task;

import org.smoothbuild.message.Info;

@SuppressWarnings("serial")
public class TaskFailedError extends Info {
  public TaskFailedError(String name) {
    super(createMessage(name));
  }

  private static String createMessage(String name) {
    return TaskCompletedInfo.createMessage(name, "FAILED");
  }
}
