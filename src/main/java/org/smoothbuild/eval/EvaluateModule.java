package org.smoothbuild.eval;

import java.io.IOException;

import javax.inject.Singleton;

import org.smoothbuild.db.Hash;
import org.smoothbuild.install.InstallationHashes;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class EvaluateModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }
}
