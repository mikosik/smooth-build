package org.smoothbuild.lang.base;

import java.lang.reflect.Method;

import org.smoothbuild.io.util.JarFile;

public class Native {
  private final Method method;
  private final JarFile jarFile;

  public Native(Method method, JarFile jarFile) {
    this.method = method;
    this.jarFile = jarFile;
  }

  public Method method() {
    return method;
  }

  public JarFile jarFile() {
    return jarFile;
  }
}
