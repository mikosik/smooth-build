package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.Type;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public class Function {
  private final String name;
  private final Type<?> type;
  private final Instantiator instantiator;

  public Function(String name, Type<?> type, Instantiator instantiator) {
    this.name = name;
    this.type = type;
    this.instantiator = instantiator;
  }

  public String name() {
    return name;
  }

  public Type<?> type() {
    return type;
  }

  public FunctionDefinition newInstance(Path resultDir) throws CreatingInstanceFailedException {
    return instantiator.newInstance(resultDir);
  }
}
