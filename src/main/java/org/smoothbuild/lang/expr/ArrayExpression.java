package org.smoothbuild.lang.expr;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.ArrayWorker;
import org.smoothbuild.task.work.TaskWorker;

import com.google.common.collect.ImmutableList;

public class ArrayExpression<T extends Value> extends Expression<Array<T>> {
  private final ArrayType<T> arrayType;

  public ArrayExpression(ArrayType<T> arrayType, ImmutableList<? extends Expression<T>> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskWorker<Array<T>> createWorker() {
    return new ArrayWorker<>(arrayType, codeLocation());
  }
}
