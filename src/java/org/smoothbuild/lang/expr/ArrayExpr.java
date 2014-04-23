package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.SArray;
import org.smoothbuild.lang.base.SArrayType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.ArrayWorker;
import org.smoothbuild.task.base.TaskWorker;

import com.google.common.collect.ImmutableList;

public class ArrayExpr<T extends SValue> extends Expr<SArray<T>> {
  private final SArrayType<T> arrayType;

  public ArrayExpr(SArrayType<T> arrayType, ImmutableList<? extends Expr<T>> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskWorker<SArray<T>> createWorker() {
    return new ArrayWorker<T>(arrayType, codeLocation());
  }
}
