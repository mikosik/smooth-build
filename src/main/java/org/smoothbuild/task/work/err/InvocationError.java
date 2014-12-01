package org.smoothbuild.task.work.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import org.smoothbuild.message.base.Message;

import com.google.common.base.Throwables;

public class InvocationError extends Message {
  public InvocationError(Throwable e) {
    super(ERROR, "Invoking function caused internal exception:\n"
        + Throwables.getStackTraceAsString(e));
  }
}
