package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.task.base.Task;

public class BoundValueExpression extends Expression {
  private final String name;

  public BoundValueExpression(String name, Location location) {
    super(location);
    this.name = name;
  }

  @Override
  public Task createTask(ObjectsDb objectsDb, Scope<Task> scope) {
    return scope.get(name);
  }
}
