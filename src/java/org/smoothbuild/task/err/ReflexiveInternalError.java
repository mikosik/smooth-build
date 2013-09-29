package org.smoothbuild.task.err;

import org.smoothbuild.message.message.CallLocation;

@SuppressWarnings("serial")
public class ReflexiveInternalError extends InvocationError {
  public ReflexiveInternalError(CallLocation callLocation, Throwable e) {
    super(callLocation, e);
  }
}
