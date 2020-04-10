package org.smoothbuild.exec.task;

import static org.smoothbuild.SmoothPaths.smoothJarPath;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.smoothbuild.db.hashed.Hash;

public class SandboxHashProvider {
  public static Hash get() {
    return Hash.of(javaPlatformHash(), smoothJarHash());
  }

  public static Hash javaPlatformHash() {
    return javaPlatformHash(System.getProperties());
  }

  // visible for testing
  static Hash javaPlatformHash(Properties properties) {
    return Hash.of(
        hash(properties, "java.vendor"),
        hash(properties, "java.version"),
        hash(properties, "java.runtime.name"),
        hash(properties, "java.runtime.version"),
        hash(properties, "java.vm.name"),
        hash(properties, "java.vm.version"));
  }

  private static Hash hash(Properties properties, String name) {
    return Hash.of(properties.getProperty(name));
  }

  public static Hash smoothJarHash() {
    return calculateHash(smoothJarPath());
  }

  private static Hash calculateHash(Path smoothJarFile) {
    try {
      return Hash.of(smoothJarFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
