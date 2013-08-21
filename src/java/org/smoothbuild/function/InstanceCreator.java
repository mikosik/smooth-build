package org.smoothbuild.function;

import org.smoothbuild.function.exc.CreatingInstanceFailedException;
import org.smoothbuild.lang.type.Path;

public interface InstanceCreator {
  public Object createInstance(Path resultDir) throws CreatingInstanceFailedException;
}
