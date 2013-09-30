package org.smoothbuild.task.err;

import org.smoothbuild.message.message.CallLocation;
import org.smoothbuild.message.message.CodeError;

import com.google.common.base.Throwables;

public class InvocationError extends CodeError {
  public InvocationError(CallLocation callLocation, Throwable e) {
    super(callLocation.location(), "Invoking function " + callLocation.name()
        + " caused internal exception:\n" + Throwables.getStackTraceAsString(e));
  }
}
