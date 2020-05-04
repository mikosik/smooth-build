package org.smoothbuild.parse.expr;

import static org.smoothbuild.exec.task.base.Task.NAME_LENGTH_LIMIT;
import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.exec.task.base.FixedTask;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.base.SString;

public class StringLiteralExpression extends Expression {
  private final SString sstring;

  public StringLiteralExpression(SString sstring, Location location) {
    super(location);
    this.sstring = sstring;
  }

  @Override
  public Task createTask(Scope<Task> scope) {
    String name = escapedAndLimitedWithEllipsis(sstring.jValue(), NAME_LENGTH_LIMIT);
    return new FixedTask(sstring, name, location());
  }
}
