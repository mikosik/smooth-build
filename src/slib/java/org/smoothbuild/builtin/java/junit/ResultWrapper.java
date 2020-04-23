package org.smoothbuild.builtin.java.junit;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.builtin.java.junit.ReflectionUtil.runReflexivelyAndCast;

import java.util.List;

import org.smoothbuild.lang.plugin.NativeApi;

public class ResultWrapper {
  private final NativeApi nativeApi;
  private final Object result;

  public ResultWrapper(NativeApi nativeApi, Object result) {
    this.nativeApi = nativeApi;
    this.result = result;
  }

  public boolean wasSuccessful() {
    return runReflexivelyAndCast(nativeApi, Boolean.class, result, "wasSuccessful");
  }

  public List<FailureWrapper> getFailures() {
    List<?> failures = runReflexivelyAndCast(nativeApi, List.class, result, "getFailures");
    return failures
        .stream()
        .map(f -> new FailureWrapper(nativeApi, f))
        .collect(toList());
  }
}
