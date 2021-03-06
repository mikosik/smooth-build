package org.smoothbuild.lang.base;

import java.lang.reflect.Method;

import org.smoothbuild.io.util.JarFile;

public class Native {
  private final Method method;
  private final boolean cacheable;
  private final JarFile jarFile;

  public Native(Method method, boolean cacheable, JarFile jarFile) {
    this.method = method;
    this.cacheable = cacheable;
    this.jarFile = jarFile;
  }

  public Method method() {
    return method;
  }

  public boolean cacheable() {
    return cacheable;
  }

  public JarFile jarFile() {
    return jarFile;
  }
}
