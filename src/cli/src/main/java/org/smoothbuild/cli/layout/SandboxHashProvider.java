package org.smoothbuild.cli.layout;

import static com.google.common.base.Preconditions.checkState;

import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.dagger.PerCommand;

@PerCommand
public class SandboxHashProvider {
  @Nullable
  private volatile Hash sandboxHash;

  @Inject
  public SandboxHashProvider() {}

  public Hash get() {
    checkState(sandboxHash != null, "SandboxHashProvider is not initialized.");
    return sandboxHash;
  }

  void set(Hash hash) {
    this.sandboxHash = hash;
  }
}
