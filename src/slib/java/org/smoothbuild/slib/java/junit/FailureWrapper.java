package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.slib.java.junit.ReflectionUtil.runReflexivelyAndCast;

public class FailureWrapper {
  private final Object failure;

  public FailureWrapper(Object failure) {
    this.failure = failure;
  }

  public String getTrace() throws JunitExc {
    return runReflexivelyAndCast(String.class, failure, "getTrace");
  }

  @Override
  public String toString() {
    return failure.toString();
  }
}
