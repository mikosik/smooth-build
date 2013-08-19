package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.FunctionDefinition;
import org.smoothbuild.lang.function.exc.CreatingInstanceFailedException;
import org.smoothbuild.lang.type.Path;

public interface Instantiator {
  public FunctionDefinition newInstance(Path resultDir) throws CreatingInstanceFailedException;
}
