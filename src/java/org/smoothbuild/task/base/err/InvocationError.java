package org.smoothbuild.task.base.err;

import static org.smoothbuild.message.message.MessageType.ERROR;

import org.smoothbuild.message.message.CodeMessage;
import org.smoothbuild.message.message.CallLocation;

import com.google.common.base.Throwables;

public class InvocationError extends CodeMessage {
  public InvocationError(CallLocation callLocation, Throwable e) {
    super(ERROR, callLocation.location(), "Invoking function " + callLocation.name()
        + " caused internal exception:\n" + Throwables.getStackTraceAsString(e));
  }
}
