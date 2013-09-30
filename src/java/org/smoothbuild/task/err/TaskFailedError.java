package org.smoothbuild.task.err;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeInfo;

public class TaskFailedError extends CodeInfo {
  public TaskFailedError(CallLocation callLocation) {
    super(callLocation.location(), createMessage(callLocation));
  }

  private static String createMessage(CallLocation callLocation) {
    return TaskCompletedInfo.createMessage(callLocation.name(), "FAILED");
  }
}
