package org.smoothbuild.exec.nativ;

import java.lang.reflect.Method;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.plugin.Caching.Level;

public class Native {
  private final Method method;
  private final Level cachingLevel;
  private final JarFile jarFile;

  public Native(Method method, Level cachingLevel, JarFile jarFile) {
    this.method = method;
    this.cachingLevel = cachingLevel;
    this.jarFile = jarFile;
  }

  public Method method() {
    return method;
  }

  public Level cachingLevel() {
    return cachingLevel;
  }

  public Hash hash() {
    return Hash.of(
        jarFile.hash(),
        Hash.of(method.getDeclaringClass().getCanonicalName()),
        Hash.of(method.getName()));
  }
}
