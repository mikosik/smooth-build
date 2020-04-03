package org.smoothbuild.parse.expr;

import java.util.List;

import org.smoothbuild.exec.comp.Algorithm;
import org.smoothbuild.exec.comp.ValueAlgorithm;
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
    Algorithm algorithm = new ValueAlgorithm(object);
    List<Task> dependencies = ImmutableList.of();
    return new Task(algorithm, dependencies, location(), true);
  }
}
