package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public class FunctionType {
  private final String name;
  private final Instantiator instantiator;

  public FunctionType(String name, Instantiator instantiator) {
    this.name = name;
    this.instantiator = instantiator;
  }

  public String name() {
    return name;
  }

  public Function newInstance(Path resultDir) throws CreatingInstanceFailedException {
    return instantiator.newInstance(resultDir);
  }
}
