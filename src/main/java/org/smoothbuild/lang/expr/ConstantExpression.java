package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.ConstantWorker;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;

public class ConstantExpression extends Expression {
  private final Value value;

  public ConstantExpression(Value value, CodeLocation codeLocation) {
    super(value.type(), Empty.expressionList(), codeLocation);
    this.value = checkNotNull(value);
  }

  @Override
  public TaskWorker createWorker() {
    return new ConstantWorker(type(), value, codeLocation());
  }
}
