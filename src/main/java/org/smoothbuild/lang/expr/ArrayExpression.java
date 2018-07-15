package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.arrayEvaluator;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, Location location) {
    super(arrayType, location);
    this.arrayType = arrayType;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    return new Dag<>(arrayEvaluator(arrayType, location()),
        createChildrenEvaluators(children, valuesDb, scope));
  }
}
