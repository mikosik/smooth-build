package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Computer.arrayComputer;

import java.util.List;

import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Computer;

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, List<? extends Expression> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  @Override
  public Computer createComputer() {
    return arrayComputer(arrayType, codeLocation());
  }
}
