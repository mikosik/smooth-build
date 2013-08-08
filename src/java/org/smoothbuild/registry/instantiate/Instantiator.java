package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public interface Instantiator {
  public Function newInstance(Path resultDir) throws CreatingInstanceFailedException;
}
