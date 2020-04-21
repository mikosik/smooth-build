package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ConstructorCallAlgorithm;
import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.NormalTask;
import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;

public class ConstructorCallExpression extends Expression {
  private final Constructor constructor;

  public ConstructorCallExpression(Constructor constructor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.constructor = constructor;
  }

  @Override
  public BuildTask createTask(Scope<BuildTask> scope) {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor);
    List<BuildTask> dependencies = childrenTasks(scope);
    return new NormalTask(algorithm, dependencies, location(), true);
  }
}
