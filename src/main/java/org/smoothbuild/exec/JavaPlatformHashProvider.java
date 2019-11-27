package org.smoothbuild.exec;

import java.util.Properties;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;

public class JavaPlatformHashProvider {
  private final Properties properties;

  @Inject
  public JavaPlatformHashProvider() {
    this(System.getProperties());
  }

  public JavaPlatformHashProvider(Properties properties) {
    this.properties = properties;
  }

  public Hash get() {
    return Hash.of(
        hash("java.vendor"),
        hash("java.version"),
        hash("java.runtime.name"),
        hash("java.runtime.version"),
        hash("java.vm.name"),
        hash("java.vm.version"));
  }

  private Hash hash(String name) {
    return Hash.of(properties.getProperty(name));
  }
}
