package org.smoothbuild.task;

import java.util.Properties;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.hash.HashCode;

public class JavaPlatformHashProvider {
  private final Properties properties;

  @Inject
  public JavaPlatformHashProvider() {
    this(System.getProperties());
  }

  public JavaPlatformHashProvider(Properties properties) {
    this.properties = properties;
  }

  public HashCode get() {
    return Hash.hashes(
        hash("java.vendor"),
        hash("java.version"),
        hash("java.runtime.name"),
        hash("java.runtime.version"),
        hash("java.vm.name"),
        hash("java.vm.version"));
  }

  private HashCode hash(String name) {
    return Hash.string(properties.getProperty(name));
  }
}
