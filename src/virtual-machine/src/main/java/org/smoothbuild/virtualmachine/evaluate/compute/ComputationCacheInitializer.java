package org.smoothbuild.virtualmachine.evaluate.compute;

import jakarta.inject.Inject;
import org.smoothbuild.common.init.Initializable;

public class ComputationCacheInitializer extends Initializable {
  private final ComputationCache computationCache;

  @Inject
  public ComputationCacheInitializer(ComputationCache computationCache) {
    super("ComputationCache");
    this.computationCache = computationCache;
  }

  @Override
  protected void executeImpl() throws Exception {
    computationCache.initialize();
  }
}
