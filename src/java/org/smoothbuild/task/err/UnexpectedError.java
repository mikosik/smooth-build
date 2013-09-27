package org.smoothbuild.task.err;

import org.smoothbuild.function.base.Name;

@SuppressWarnings("serial")
public class UnexpectedError extends InvocationError {
  public UnexpectedError(Name name, Throwable e) {
    super(name, e);
  }
}
