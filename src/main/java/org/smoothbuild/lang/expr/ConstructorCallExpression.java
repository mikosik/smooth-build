package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Constructor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.Computation;
import org.smoothbuild.task.base.ConstructorCallComputation;
import org.smoothbuild.task.base.Task;

public class ConstructorCallExpression extends Expression {
  private final Constructor constructor;

  public ConstructorCallExpression(Constructor constructor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.constructor = constructor;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    Computation computation = new ConstructorCallComputation(constructor);
    List<Task> dependencies = childrenTasks(scope);
    return new Task(computation, constructor.name(), true, dependencies, location());
  }
}
