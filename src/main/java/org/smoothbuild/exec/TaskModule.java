package org.smoothbuild.exec;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TaskModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash(
      JavaPlatformHashProvider javaPlatformHashProvider,
      SmoothJarHashProvider smoothJarHashProvider) {
    return Hash.of(
        javaPlatformHashProvider.get(),
        smoothJarHashProvider.get());
  }
}
