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
  public Hash provideSandboxHash() {
    return SandboxHashProvider.get();
  }
}
