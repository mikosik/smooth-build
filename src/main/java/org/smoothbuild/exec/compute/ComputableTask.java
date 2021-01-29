package org.smoothbuild.exec.compute;

import java.util.List;

import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.lang.base.define.Location;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.plugin.Caching.Level;

public abstract class ComputableTask extends Task {
  private final TaskKind kind;
  private final Algorithm algorithm;
  private final Level cachingLevel;

  public ComputableTask(TaskKind kind, Type type, String name, Algorithm algorithm,
      List<? extends Task> dependencies, Location location, Level cachingLevel) {
    super(type, name, dependencies, location);
    this.kind = kind;
    this.algorithm = algorithm;
    this.cachingLevel = cachingLevel;
  }

  @Override
  public TaskKind kind() {
    return kind;
  }

  public Algorithm algorithm() {
    return algorithm;
  }

  public Level cachingLevel() {
    return cachingLevel;
  }

  @Override
  public String toString() {
    return "Task(" + algorithm.getClass().getCanonicalName() + ")";
  }
}
