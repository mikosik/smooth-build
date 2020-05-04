package org.smoothbuild.exec.task;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.install.InstallationHashes;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class TaskModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash() {
    return InstallationHashes.sandboxHash();
  }
}
