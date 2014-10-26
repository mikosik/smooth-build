package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.ConstantWorker;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;

public class ConstantExpr<T extends Value> extends Expression<T> {
  private final T value;

  public ConstantExpr(Type<T> type, T value, CodeLocation codeLocation) {
    super(type, Empty.exprList(), codeLocation);
    this.value = checkNotNull(value);
  }

  @Override
  public TaskWorker<T> createWorker() {
    return new ConstantWorker<>(type(), value, codeLocation());
  }
}
