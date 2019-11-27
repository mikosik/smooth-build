package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.ConstructorCallComputation;
import org.smoothbuild.exec.task.Task;
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
    Computation computation = new ConstructorCallComputation(constructor);
    List<Task> dependencies = childrenTasks(scope);
    return new Task(computation, constructor.name(), true, dependencies, location());
  }
}
