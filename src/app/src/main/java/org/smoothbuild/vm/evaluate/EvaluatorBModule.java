package org.smoothbuild.vm.evaluate;

import java.io.IOException;

import org.smoothbuild.fs.install.InstallationHashes;
import org.smoothbuild.vm.bytecode.hashed.Hash;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import jakarta.inject.Singleton;

public class EvaluatorBModule extends AbstractModule {
  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }
}
