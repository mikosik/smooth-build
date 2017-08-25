package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Computer.arrayComputer;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.CodeLocation;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.task.base.Computer;

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, List<Expression> elements,
      CodeLocation codeLocation) {
    super(arrayType, elements, codeLocation);
    this.arrayType = arrayType;
  }

  public Computer createComputer(ValuesDb valuesDb) {
    return arrayComputer(arrayType, codeLocation(), createDependenciesComputers(valuesDb));
  }
}
