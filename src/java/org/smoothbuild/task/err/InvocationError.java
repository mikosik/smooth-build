package org.smoothbuild.task.err;

import static org.smoothbuild.message.listen.MessageType.ERROR;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.TaskLocation;

import com.google.common.base.Throwables;

public class InvocationError extends CodeMessage {
  public InvocationError(TaskLocation taskLocation, Throwable e) {
    super(ERROR, taskLocation.location(), "Invoking function " + taskLocation.name()
        + " caused internal exception:\n" + Throwables.getStackTraceAsString(e));
  }
}
