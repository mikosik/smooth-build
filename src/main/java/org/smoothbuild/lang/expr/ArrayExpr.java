package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.ArrayWorker;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;

public class ArrayExpr<T extends SValue> extends Expr<Array<T>> {
  private final ArrayType<T> arrayType;

  public ArrayExpr(ArrayType<T> arrayType, ImmutableList<? extends Expr<T>> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskWorker<Array<T>> createWorker() {
    return new ArrayWorker<>(arrayType, codeLocation());
  }
}
