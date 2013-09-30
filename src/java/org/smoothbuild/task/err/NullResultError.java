package org.smoothbuild.task.err;

import org.smoothbuild.message.message.TaskLocation;
import org.smoothbuild.message.message.ErrorCodeMessage;

public class NullResultError extends ErrorCodeMessage {
  public NullResultError(TaskLocation taskLocation) {
    super(taskLocation.location(), "Faulty implementation of " + taskLocation.name()
        + " function : 'null' was returned but no error reported.");
  }
}
