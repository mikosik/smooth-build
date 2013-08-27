package org.smoothbuild.function.plugin;

import org.smoothbuild.function.plugin.exc.CreatingInstanceFailedException;
import org.smoothbuild.plugin.Path;

public interface InstanceCreator {
  public Object createInstance(Path resultDir) throws CreatingInstanceFailedException;
}
