package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.slib.java.junit.ReflectionUtil.runReflexivelyAndCast;

import org.smoothbuild.lang.plugin.NativeApi;

public class FailureWrapper {
  private final NativeApi nativeApi;
  private final Object failure;

  public FailureWrapper(NativeApi nativeApi, Object failure) {
    this.nativeApi = nativeApi;
    this.failure = failure;
  }

  public String getTrace() {
    return runReflexivelyAndCast(nativeApi, String.class, failure, "getTrace");
  }

  @Override
  public String toString() {
    return failure.toString();
  }
}
