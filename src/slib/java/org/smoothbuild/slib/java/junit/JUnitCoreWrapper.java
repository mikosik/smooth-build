package org.smoothbuild.slib.java.junit;

import static org.smoothbuild.slib.java.junit.ReflectionUtil.runReflexively;

import org.smoothbuild.vm.evaluate.plugin.NativeApi;

public class JUnitCoreWrapper {
  private final NativeApi nativeApi;
  private final Object jUnitCore;

  public static JUnitCoreWrapper newInstance(NativeApi nativeApi, Class<?> clazz)
      throws JunitExc {
    return new JUnitCoreWrapper(nativeApi, ReflectionUtil.newInstance(clazz));
  }

  public JUnitCoreWrapper(NativeApi nativeApi, Object jUnitCore) {
    this.nativeApi = nativeApi;
    this.jUnitCore = jUnitCore;
  }

  public ResWrapper run(Class<?> testClass) throws JunitExc {
    return new ResWrapper(
        runReflexively(jUnitCore, "run", new Object[] {new Class<?>[] {testClass}}));
  }
}
