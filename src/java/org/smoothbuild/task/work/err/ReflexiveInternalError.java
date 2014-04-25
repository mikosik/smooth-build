package org.smoothbuild.task.work.err;

@SuppressWarnings("serial")
public class ReflexiveInternalError extends InvocationError {
  public ReflexiveInternalError(Throwable e) {
    super(e);
  }
}
