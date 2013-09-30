package org.smoothbuild.task.err;

import org.smoothbuild.message.message.TaskLocation;
import org.smoothbuild.message.message.ErrorCodeMessage;

import com.google.common.base.Throwables;

public class InvocationError extends ErrorCodeMessage {
  public InvocationError(TaskLocation taskLocation, Throwable e) {
    super(taskLocation.location(), "Invoking function " + taskLocation.name()
        + " caused internal exception:\n" + Throwables.getStackTraceAsString(e));
  }
}
