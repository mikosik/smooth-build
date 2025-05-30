package org.smoothbuild.cli.layout;

import jakarta.inject.Inject;
import org.smoothbuild.common.init.Initializable;

public class SandboxHashProviderInitializer extends Initializable {
  private final InstallationHashes installationHashes;
  private final SandboxHashProvider sandboxHashProvider;

  @Inject
  public SandboxHashProviderInitializer(
      InstallationHashes installationHashes, SandboxHashProvider sandboxHashProvider) {
    super("SandboxHashProvider");
    this.installationHashes = installationHashes;
    this.sandboxHashProvider = sandboxHashProvider;
  }

  @Override
  protected void executeImpl() throws Exception {
    sandboxHashProvider.set(installationHashes.sandboxNode().hash());
  }
}
