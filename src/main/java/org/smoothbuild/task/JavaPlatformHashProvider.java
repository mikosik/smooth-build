package org.smoothbuild.task;

import static org.smoothbuild.db.hashed.Hash.newHasher;

import java.util.Properties;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;

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
        .putBytes(hash("java.vendor").asBytes())
        .putBytes(hash("java.version").asBytes())
        .putBytes(hash("java.runtime.name").asBytes())
        .putBytes(hash("java.runtime.version").asBytes())
        .putBytes(hash("java.vm.name").asBytes())
        .putBytes(hash("java.vm.version").asBytes())
        .hash();
  }

  private HashCode hash(String name) {
    return Hash.string(properties.getProperty(name));
  }
}
