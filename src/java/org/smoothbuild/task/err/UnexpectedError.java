package org.smoothbuild.task.err;

import org.smoothbuild.message.message.CallLocation;

public class UnexpectedError extends InvocationError {
  public UnexpectedError(CallLocation callLocation, Throwable e) {
    super(callLocation, e);
  }
}
