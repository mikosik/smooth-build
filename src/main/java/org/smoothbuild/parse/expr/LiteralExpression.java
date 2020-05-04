package org.smoothbuild.parse.expr;

import static org.smoothbuild.exec.task.base.Task.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.exec.task.base.FixedTask;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;

public class LiteralExpression extends Expression {
  private final SObject object;

  public LiteralExpression(SObject object, Location location) {
    super(location);
    this.object = object;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    String name = escapedAndLimitedWithEllipsis(((SString) object).jValue(), NAME_LENGTH_LIMIT);
    return new FixedTask(object, name, location());
  }
}
