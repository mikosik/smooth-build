package org.smoothbuild.task;

import static org.smoothbuild.db.hashed.Hash.newHasher;

import java.util.Properties;

import javax.inject.Inject;

import com.google.common.base.Charsets;
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
    return newHasher()
        .putString(properties.getProperty("java.vendor"), Charsets.UTF_8)
        .putString(properties.getProperty("java.version"), Charsets.UTF_8)
        .putString(properties.getProperty("java.runtime.name"), Charsets.UTF_8)
        .putString(properties.getProperty("java.runtime.version"), Charsets.UTF_8)
        .putString(properties.getProperty("java.vm.name"), Charsets.UTF_8)
        .putString(properties.getProperty("java.vm.version"), Charsets.UTF_8)
        .hash();
  }
}
