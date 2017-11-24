package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.junit.ReflectionUtil.runReflexively;

public class JUnitCoreWrapper {
  private final Object jUnitCore;

  public static JUnitCoreWrapper newInstance(Class<?> clazz) {
    return new JUnitCoreWrapper(ReflectionUtil.newInstance(clazz));
  }

  public JUnitCoreWrapper(Object jUnitCore) {
    this.jUnitCore = jUnitCore;
  }

  public ResultWrapper run(Class<?> testClass) {
    return new ResultWrapper(runReflexively(jUnitCore, "run",
        new Object[] { new Class<?>[] { testClass } }));
  }
}
