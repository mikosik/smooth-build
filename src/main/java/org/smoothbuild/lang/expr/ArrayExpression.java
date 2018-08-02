package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.arrayEvaluator;

import java.util.List;
import java.util.function.IntFunction;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.TypeChooser;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class ArrayExpression extends Expression {
  private final TypeChooser<ConcreteType> evaluatorTypeChooser;

  public ArrayExpression(ArrayType arrayType, TypeChooser<ConcreteType> evaluatorTypeChooser,
      Location location) {
    super(arrayType, location);
    this.evaluatorTypeChooser = evaluatorTypeChooser;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    List<Dag<Evaluator>> childrenEvaluators = evaluators(children, valuesDb, scope);
    IntFunction<ConcreteType> childrenType = i -> childrenEvaluators.get(i).elem().type();
    return new Dag<>(arrayEvaluator(
        (ConcreteArrayType) evaluatorTypeChooser.choose(childrenType), location()),
        childrenEvaluators);
  }
}
