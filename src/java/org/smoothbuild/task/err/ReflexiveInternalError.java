package org.smoothbuild.task.err;

import org.smoothbuild.message.message.TaskLocation;

public class ReflexiveInternalError extends InvocationError {
  public ReflexiveInternalError(TaskLocation taskLocation, Throwable e) {
    super(taskLocation, e);
  }
}
