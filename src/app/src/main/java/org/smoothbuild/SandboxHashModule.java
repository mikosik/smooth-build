package org.smoothbuild;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import jakarta.inject.Singleton;
import java.io.IOException;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.layout.InstallationHashes;
import org.smoothbuild.virtualmachine.wire.Sandbox;

public class SandboxHashModule extends AbstractModule {
  @Provides
  @Singleton
  @Sandbox
  public Hash provideSandboxHash(InstallationHashes installationHashes) throws IOException {
    return installationHashes.sandboxNode().hash();
  }
}
