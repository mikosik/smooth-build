package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.junit.ReflectionUtil.runReflexively;

import org.smoothbuild.lang.plugin.NativeApi;

public class JUnitCoreWrapper {
  private final NativeApi nativeApi;
  private final Object jUnitCore;

  public static JUnitCoreWrapper newInstance(NativeApi nativeApi, Class<?> clazz) {
    return new JUnitCoreWrapper(nativeApi, ReflectionUtil.newInstance(nativeApi, clazz));
  }

  public JUnitCoreWrapper(NativeApi nativeApi, Object jUnitCore) {
    this.nativeApi = nativeApi;
    this.jUnitCore = jUnitCore;
  }

  public ResultWrapper run(Class<?> testClass) {
    return new ResultWrapper(nativeApi, runReflexively(nativeApi, jUnitCore, "run",
        new Object[] { new Class<?>[] { testClass } }));
  }
}
