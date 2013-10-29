package org.smoothbuild.task.base.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.Message;

import com.google.common.base.Throwables;

public class InvocationError extends Message {
  public InvocationError(Throwable e) {
    super(ERROR, "Invoking function caused internal exception:\n"
        + Throwables.getStackTraceAsString(e));
  }
}
