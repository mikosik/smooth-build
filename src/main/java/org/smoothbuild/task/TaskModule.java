package org.smoothbuild.task;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TaskModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @RuntimeHash
  public Hash provideRuntimeHash(
      JavaPlatformHashProvider javaPlatformHashProvider,
      SmoothJarHashProvider smoothJarHashProvider) {
    return Hash.of(
        javaPlatformHashProvider.get(),
        smoothJarHashProvider.get());
  }
}
