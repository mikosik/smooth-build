package org.smoothbuild.exec.nativ;

import java.lang.reflect.Method;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.util.JarFile;

public class Native {
  private final String name;
  private final Method method;
  private final boolean cacheable;
  private final JarFile jarFile;

  public Native(String name, Method method, boolean cacheable, JarFile jarFile) {
    this.name = name;
    this.method = method;
    this.cacheable = cacheable;
    this.jarFile = jarFile;
  }

  public String name() {
    return name;
  }

  public Method method() {
    return method;
  }

  public boolean cacheable() {
    return cacheable;
  }

  public Hash hash() {
    return Hash.of(
        jarFile.hash(),
        Hash.of(method.getDeclaringClass().getCanonicalName()),
        Hash.of(method.getName()));
  }
}
