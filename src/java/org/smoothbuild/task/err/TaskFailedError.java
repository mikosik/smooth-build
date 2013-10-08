package org.smoothbuild.task.err;

import static org.smoothbuild.message.message.MessageType.INFO;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.CallLocation;

public class TaskFailedError extends CodeMessage {
  public TaskFailedError(CallLocation callLocation) {
    super(INFO, callLocation.location(), createMessage(callLocation));
  }

  private static String createMessage(CallLocation callLocation) {
    return TaskCompletedInfo.createMessage(callLocation.name(), "FAILED");
  }
}
