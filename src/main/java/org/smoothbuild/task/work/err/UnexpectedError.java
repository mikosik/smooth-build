package org.smoothbuild.task.work.err;

@SuppressWarnings("serial")
public class UnexpectedError extends InvocationError {
  public UnexpectedError(Throwable e) {
    super(e);
  }
}
