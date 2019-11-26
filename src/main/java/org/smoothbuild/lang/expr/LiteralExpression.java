package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.task.base.Computation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.base.ValueComputation;

import com.google.common.collect.ImmutableList;

public class LiteralExpression extends Expression {
  private final SObject object;

  public LiteralExpression(SObject object, Location location) {
    super(location);
    this.object = object;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    Computation computation = new ValueComputation(object);
    List<Task> dependencies = ImmutableList.of();
    return new Task(computation, object.type().name(), true, dependencies, location());
  }
}
