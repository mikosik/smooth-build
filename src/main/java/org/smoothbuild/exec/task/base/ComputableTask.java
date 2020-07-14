package org.smoothbuild.exec.task.base;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.type.ConcreteType;

public abstract class ComputableTask extends Task {
  public final Algorithm algorithm;
  public final boolean cacheable;

  public ComputableTask(ConcreteType type, String algorithmDescription, Algorithm algorithm,
      List<? extends Task> dependencies, Location location, boolean cacheable) {
    super(type, algorithmDescription, dependencies, location);
    this.algorithm = algorithm;
    this.cacheable = cacheable;
  }

  @Override
  public String name() {
    return algorithm.name();
  }

  @Override
  public TaskKind kind() {
    return algorithm.kind();
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  public boolean cacheable() {
    return cacheable;
  }

  @Override
  public String toString() {
    return "Task(" + algorithm.name() + ")";
  }
}
