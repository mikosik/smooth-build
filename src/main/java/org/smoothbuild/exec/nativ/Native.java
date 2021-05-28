package org.smoothbuild.exec.nativ;

import java.lang.reflect.Method;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.util.JarFile;
import org.smoothbuild.plugin.Caching.Scope;

public class Native {
  private final Method method;
  private final Scope cachingScope;
  private final JarFile jarFile;

  public Native(Method method, Scope cachingScope, JarFile jarFile) {
    this.method = method;
    this.cachingScope = cachingScope;
    this.jarFile = jarFile;
  }

  public Method method() {
    return method;
  }

  public Scope cachingLevel() {
    return cachingScope;
  }

  public Hash hash() {
    return Hash.of(
        jarFile.hash(),
        Hash.of(method.getDeclaringClass().getCanonicalName()),
        Hash.of(method.getName()));
  }
}
