package org.smoothbuild.task.err;

import org.smoothbuild.message.message.TaskLocation;

public class UnexpectedError extends InvocationError {
  public UnexpectedError(TaskLocation taskLocation, Throwable e) {
    super(taskLocation, e);
  }
}
