package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.task.work.TaskWorker.defaultValueWorker;

import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;

public class DefaultValueExpression extends Expression {
  private final Value value;

  public DefaultValueExpression(Value value, CodeLocation codeLocation) {
    super(value.type(), Empty.expressionList(), codeLocation);
    this.value = checkNotNull(value);
  }

  @Override
  public TaskWorker createWorker() {
    return defaultValueWorker(type(), value, codeLocation());
  }
}
