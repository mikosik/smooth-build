package org.smoothbuild.lang.expr;

import java.util.List;

import org.smoothbuild.lang.base.ArrayType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.ArrayWorker;
import org.smoothbuild.task.work.TaskWorker;

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, List<? extends Expression> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public TaskWorker createWorker() {
    return new ArrayWorker(arrayType, codeLocation());
  }
}
