package org.smoothbuild.parse.expr;

import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;

public class BoundValueExpression extends Expression {
  private final String name;

  public BoundValueExpression(String name, Location location) {
    super(location);
    this.name = name;
  }

  @Override
  public BuildTask createTask(Scope<BuildTask> scope) {
    return scope.get(name);
  }
}
