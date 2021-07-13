package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.plan.TaskSupplier;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public abstract class ComputableTask extends Task {
  private final Algorithm algorithm;

  public ComputableTask(TaskKind kind, Type type, String name, Algorithm algorithm,
      List<? extends TaskSupplier> dependencies, Location location) {
    super(kind, type, name, dependencies, location);
    this.algorithm = algorithm;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  @Override
  public String toString() {
    return "Task(" + algorithm.getClass().getCanonicalName() + ")";
  }
}
