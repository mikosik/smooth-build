package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.lang.base.Accessor;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.task.base.AccessorCallComputation;
import org.smoothbuild.task.base.Computation;
import org.smoothbuild.task.base.Task;

public class AccessorCallExpression extends Expression {
  private final Accessor accessor;

  public AccessorCallExpression(Accessor accessor, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.accessor = accessor;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    Computation computation = new AccessorCallComputation(accessor);
    List<Task> dependencies = childrenTasks(scope);
    return new Task(computation, accessor.name(), true, dependencies, location());
  }
}
