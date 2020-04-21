package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.exec.comp.AccessorCallAlgorithm;
import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.NormalTask;
import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;

public class AccessorCallExpression extends Expression {
  private final Accessor accessor;

  public AccessorCallExpression(Accessor accessor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.accessor = accessor;
  }

  @Override
  public BuildTask createTask(Scope<BuildTask> scope) {
    Algorithm algorithm = new AccessorCallAlgorithm(accessor);
    List<BuildTask> dependencies = childrenTasks(scope);
    return new NormalTask(algorithm, dependencies, location(), true);
  }
}
