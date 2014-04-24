package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ConstantWorker;
import org.smoothbuild.task.base.TaskWorker;
import org.smoothbuild.util.Empty;

public class ConstantExpr<T extends SValue> extends Expr<T> {
  private final T value;

  public ConstantExpr(SType<T> type, T value, CodeLocation codeLocation) {
    super(type, Empty.exprList(), codeLocation);
    this.value = checkNotNull(value);
  }

  @Override
  public TaskWorker<T> createWorker() {
    return new ConstantWorker<>(type(), value, codeLocation());
  }
}
