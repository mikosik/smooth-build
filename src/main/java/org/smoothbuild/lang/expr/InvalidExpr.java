package org.smoothbuild.lang.expr;

import static com.google.common.base.Preconditions.checkNotNull;

import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.err.CannotCreateTaskWorkerFromInvalidExprError;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.util.Empty;

public class InvalidExpr<T extends Value> extends Expr<T> {
  private final SType<T> type;

  public InvalidExpr(SType<T> type, CodeLocation codeLocation) {
    super(type, Empty.exprList(), codeLocation);
    this.type = checkNotNull(type);
  }

  @Override
  public SType<T> type() {
    return type;
  }

  @Override
  public TaskWorker<T> createWorker() {
    throw new CannotCreateTaskWorkerFromInvalidExprError();
  }
}
