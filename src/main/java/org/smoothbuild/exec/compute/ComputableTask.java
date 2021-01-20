package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public abstract class ComputableTask extends Task {
  private final TaskKind kind;
  private final Algorithm algorithm;
  private final boolean cacheable;

  public ComputableTask(TaskKind kind, Type type, String name, Algorithm algorithm,
      List<? extends Task> dependencies, Location location, boolean cacheable) {
    super(type, name, dependencies, location);
    this.kind = kind;
    this.algorithm = algorithm;
    this.cacheable = cacheable;
  }

  @Override
  public TaskKind kind() {
    return kind;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  public boolean cacheable() {
    return cacheable;
  }

  @Override
  public String toString() {
    return "Task(" + algorithm.getClass().getCanonicalName() + ")";
  }
}
