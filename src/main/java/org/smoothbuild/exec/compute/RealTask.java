package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

/**
 * Subclasses of this class must be immutable.
 */
public abstract class RealTask extends AbstractTask {
  public static final int NAME_LENGTH_LIMIT = 40;

  public RealTask(TaskKind kind, Type type, String name, List<Task> dependencies,
      Location location) {
    super(kind, type, name, dependencies, location);
  }
}
