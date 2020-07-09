package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ConstructorCallAlgorithm;
import org.smoothbuild.exec.task.base.NormalTask;
import org.smoothbuild.exec.task.base.Task;
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
  public Task createTask(Scope<Task> scope) {
    Algorithm algorithm = new ConstructorCallAlgorithm(constructor);
    List<Task> dependencies = childrenTasks(scope);
    return new NormalTask(algorithm, dependencies, location(), true);
  }

  @Override
  public <T> T visit(ExpressionVisitor<T> visitor) {
    return visitor.visit(this);
  }
}
