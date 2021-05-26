package org.smoothbuild.exec.plan;

import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.base.type.Type;
import org.smoothbuild.lang.expr.ExpressionVisitorException;
import org.smoothbuild.util.MemoizingSupplier;
import org.smoothbuild.util.ThrowingSupplier;

public record TaskSupplier(Type type, ThrowingSupplier<Task, ExpressionVisitorException> supplier) {
  public TaskSupplier(Type type, ThrowingSupplier<Task, ExpressionVisitorException> supplier) {
    this.type = type;
    this.supplier = new MemoizingSupplier<>(supplier);
  }

  public Task getTask() throws ExpressionVisitorException {
    return supplier.get();
  }
}
