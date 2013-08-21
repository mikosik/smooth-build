package org.smoothbuild.registry.instantiate;

import org.smoothbuild.lang.type.Path;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public interface InstanceCreator {
  public Object createInstance(Path resultDir) throws CreatingInstanceFailedException;
}
