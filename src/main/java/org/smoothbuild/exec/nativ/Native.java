package org.smoothbuild.exec.nativ;

import java.lang.reflect.Method;

import org.smoothbuild.db.hashed.Hash;
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

  public Hash hash() {
    return Hash.of(
        jarFile.hash(),
        Hash.of(method.getDeclaringClass().getCanonicalName()),
        Hash.of(method.getName()));
  }
}
