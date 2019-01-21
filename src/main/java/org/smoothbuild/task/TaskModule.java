package org.smoothbuild.task;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;

import com.google.common.hash.HashCode;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TaskModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @RuntimeHash
  public HashCode provideRuntimeHash(
      JavaPlatformHashProvider javaPlatformHashProvider,
      SmoothJarHashProvider smoothJarHashProvider) {
    return Hash.hashes(
        javaPlatformHashProvider.get(),
        smoothJarHashProvider.get());
  }
}
