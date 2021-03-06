package org.smoothbuild.exec;

import java.io.IOException;

import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.install.InstallationHashes;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class ExecuteModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }
}
