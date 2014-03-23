package org.smoothbuild.task.base.err;

@SuppressWarnings("serial")
public class ReflexiveInternalError extends InvocationError {
  public ReflexiveInternalError(Throwable e) {
    super(e);
  }
}
