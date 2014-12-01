package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.Array;
import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.lang.base.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.ArrayWorker;
import org.smoothbuild.task.work.TaskWorker;

public class ArrayExpression<T extends Value> extends Expression<Array<T>> {
  private final ArrayType<T> arrayType;

  public ArrayExpression(ArrayType<T> arrayType, List<? extends Expression<T>> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskWorker<Array<T>> createWorker() {
    return new ArrayWorker<>(arrayType, codeLocation());
  }
}
