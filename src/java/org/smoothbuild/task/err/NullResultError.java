package org.smoothbuild.task.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.TaskLocation;

public class NullResultError extends CodeMessage {
  public NullResultError(TaskLocation taskLocation) {
    super(ERROR, taskLocation.location(), "Faulty implementation of " + taskLocation.name()
        + " function : 'null' was returned but no error reported.");
  }
}
