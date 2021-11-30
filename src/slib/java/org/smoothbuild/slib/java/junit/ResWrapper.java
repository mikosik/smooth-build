package org.smoothbuild.slib.java.junit;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.slib.java.junit.ReflectionUtil.runReflexivelyAndCast;

import java.util.List;

public class ResWrapper {
  private final Object result;

  public ResWrapper(Object result) {
    this.result = result;
  }

  public boolean wasSuccessful() throws JunitExc {
    return runReflexivelyAndCast(Boolean.class, result, "wasSuccessful");
  }

  public List<FailureWrapper> getFailures() throws JunitExc {
    List<?> failures = runReflexivelyAndCast(List.class, result, "getFailures");
    return failures
        .stream()
        .map(FailureWrapper::new)
        .collect(toList());
  }
}
