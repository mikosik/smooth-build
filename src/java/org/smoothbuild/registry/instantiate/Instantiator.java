package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public interface Instantiator {
  public FunctionDefinition newInstance(Path resultDir) throws CreatingInstanceFailedException;
}
