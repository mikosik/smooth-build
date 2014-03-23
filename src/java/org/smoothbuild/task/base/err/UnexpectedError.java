package org.smoothbuild.task.base.err;

@SuppressWarnings("serial")
public class UnexpectedError extends InvocationError {
  public UnexpectedError(Throwable e) {
    super(e);
  }
}
