package org.smoothbuild.parse.expr;

import static org.smoothbuild.util.Strings.escapedAndLimitedWithEllipsis;

import org.smoothbuild.exec.task.base.BuildTask;
import org.smoothbuild.exec.task.base.FixedTask;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.SString;

public class LiteralExpression extends Expression {
  private final SObject object;
  private final Array messages;

  public LiteralExpression(SObject object, Array messages, Location location) {
    super(location);
    this.object = object;
    this.messages = messages;
  }

  @Override
  public BuildTask createTask(Scope<BuildTask> scope) {
    String name = escapedAndLimitedWithEllipsis(((SString) this.object).jValue(), 20);
    return new FixedTask(object, name, messages, location());
  }
}
