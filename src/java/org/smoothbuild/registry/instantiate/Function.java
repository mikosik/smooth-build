package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public class Function {
  private final String name;
  private final Instantiator instantiator;

  public Function(String name, Instantiator instantiator) {
    this.name = name;
    this.instantiator = instantiator;
  }

  public String name() {
    return name;
  }

  public FunctionDefinition newInstance(Path resultDir) throws CreatingInstanceFailedException {
    return instantiator.newInstance(resultDir);
  }
}
