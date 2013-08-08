package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.type.Path;

public class FunctionInstanceId {
  private final Path resultDir;

  public FunctionInstanceId(Path resultDir) {
    this.resultDir = resultDir;
  }

  public Path resultDir() {
    return resultDir;
  }
}
