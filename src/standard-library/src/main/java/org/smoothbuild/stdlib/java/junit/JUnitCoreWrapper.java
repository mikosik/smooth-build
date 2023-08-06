package org.smoothbuild.stdlib.java.junit;

import static org.smoothbuild.stdlib.java.junit.ReflectionUtil.runReflexively;

import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class JUnitCoreWrapper {
  private final NativeApi nativeApi;
  private final Object jUnitCore;

  public static JUnitCoreWrapper newInstance(NativeApi nativeApi, Class<?> clazz)
      throws JunitException {
    return new JUnitCoreWrapper(nativeApi, ReflectionUtil.newInstance(clazz));
  }

  public JUnitCoreWrapper(NativeApi nativeApi, Object jUnitCore) {
    this.nativeApi = nativeApi;
    this.jUnitCore = jUnitCore;
  }

  public ResWrapper run(Class<?> testClass) throws JunitException {
    return new ResWrapper(
        runReflexively(jUnitCore, "run", new Object[] {new Class<?>[] {testClass}}));
  }
}
