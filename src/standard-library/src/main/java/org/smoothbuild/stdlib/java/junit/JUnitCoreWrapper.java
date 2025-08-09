package org.smoothbuild.stdlib.java.junit;

import static org.smoothbuild.stdlib.java.junit.ReflectionUtil.runReflexively;

public class JUnitCoreWrapper {
  private final Object jUnitCore;

  public static JUnitCoreWrapper newInstance(Class<?> clazz) throws JunitException {
    return new JUnitCoreWrapper(ReflectionUtil.newInstance(clazz));
  }

  public JUnitCoreWrapper(Object jUnitCore) {
    this.jUnitCore = jUnitCore;
  }

  public ResWrapper run(Class<?> testClass) throws JunitException {
    return new ResWrapper(
        runReflexively(jUnitCore, "run", new Object[] {new Class<?>[] {testClass}}));
  }
}
