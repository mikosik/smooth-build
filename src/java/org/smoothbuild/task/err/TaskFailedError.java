package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.Info;

@SuppressWarnings("serial")
public class TaskFailedError extends Info {
  public TaskFailedError(Name name) {
    super(createMessage(name));
  }

  private static String createMessage(Name name) {
    return TaskCompletedInfo.createMessage(name, "FAILED");
  }
}
