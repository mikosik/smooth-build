package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;

public abstract class ComputableTask extends Task {
  private final TaskKind kind;
  private final Algorithm algorithm;

  public ComputableTask(TaskKind kind, Type type, String name, Algorithm algorithm,
      List<? extends Task> dependencies, Location location) {
    super(type, name, dependencies, location);
    this.kind = kind;
    this.algorithm = algorithm;
  }

  @Override
  public TaskKind kind() {
    return kind;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  @Override
  public String toString() {
    return "Task(" + algorithm.getClass().getCanonicalName() + ")";
  }
}
