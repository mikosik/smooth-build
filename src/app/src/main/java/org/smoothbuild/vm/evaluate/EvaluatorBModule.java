package org.smoothbuild.vm.evaluate;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.smoothbuild.filesystem.install.InstallationHashes;
import org.smoothbuild.vm.bytecode.hashed.Hash;

public class EvaluatorBModule extends AbstractModule {
  @Provides
  @Singleton
  @SandboxHash
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }
}
