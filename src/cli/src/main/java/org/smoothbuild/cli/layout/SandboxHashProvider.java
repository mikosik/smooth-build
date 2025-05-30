package org.smoothbuild.cli.layout;

import jakarta.inject.Inject;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.dagger.PerCommand;

@PerCommand
public class SandboxHashProvider {
  private volatile Hash sandboxHash;

  @Inject
  public SandboxHashProvider() {}

  public Hash get() {
    return sandboxHash;
  }

  void set(Hash hash) {
    this.sandboxHash = hash;
  }
}
