package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.work.TaskWorker.arrayWorker;

import java.util.List;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.message.base.CodeLocation;
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
    return arrayWorker(arrayType, codeLocation());
  }
}
