package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.arrayEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.function.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.task.base.Evaluator;

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, List<Expression> elements,
      Location location) {
    super(arrayType, elements, location);
    this.arrayType = arrayType;
  }

  public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
    return arrayEvaluator(arrayType, location(), createDependenciesEvaluator(valuesDb, scope));
  }
}
