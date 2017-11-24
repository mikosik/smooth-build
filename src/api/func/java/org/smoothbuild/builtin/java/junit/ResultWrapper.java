package org.smoothbuild.builtin.java.junit;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.builtin.java.junit.ReflectionUtil.runReflexivelyAndCast;

import java.util.List;

public class ResultWrapper {
  private final Object result;

  public ResultWrapper(Object result) {
    this.result = result;
  }

  public boolean wasSuccessful() {
    return runReflexivelyAndCast(Boolean.class, result, "wasSuccessful");
  }

  public List<FailureWrapper> getFailures() {
    List<?> failures = runReflexivelyAndCast(List.class, result, "getFailures");
    return failures
        .stream()
        .map(FailureWrapper::new)
        .collect(toList());
  }
}
