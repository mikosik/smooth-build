package org.smoothbuild.exec.task.base;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.object.type.ConcreteType;

public abstract class ComputableTask extends BuildTask {
  public final Algorithm algorithm;
  public final boolean cacheable;

  public ComputableTask(Algorithm algorithm, List<? extends BuildTask> dependencies, Location location,
      boolean cacheable) {
    super(dependencies, location);
    this.algorithm = algorithm;
    this.cacheable = cacheable;
  }

  @Override
  public String name() {
    return algorithm.name();
  }

  @Override
  public String description() {
    return algorithm.description();
  }

  @Override
  public ConcreteType type() {
    return algorithm.type();
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
