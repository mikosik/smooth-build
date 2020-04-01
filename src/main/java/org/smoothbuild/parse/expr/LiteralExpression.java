package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.exec.comp.Computation;
import org.smoothbuild.exec.comp.ValueComputation;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.base.SObject;

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
    return new Task(computation, dependencies, location(), true);
  }
}
