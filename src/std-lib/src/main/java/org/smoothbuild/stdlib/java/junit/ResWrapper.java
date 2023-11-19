package org.smoothbuild.stdlib.java.junit;

import static org.smoothbuild.stdlib.java.junit.ReflectionUtil.runReflexivelyAndCast;

import java.util.List;

public class ResWrapper {
  private final Object result;

  public ResWrapper(Object result) {
    this.result = result;
  }

  public boolean wasSuccessful() throws JunitException {
    return runReflexivelyAndCast(Boolean.class, result, "wasSuccessful");
  }

  public List<FailureWrapper> getFailures() throws JunitException {
    List<?> failures = runReflexivelyAndCast(List.class, result, "getFailures");
    return failures.stream().map(FailureWrapper::new).toList();
  }
}
