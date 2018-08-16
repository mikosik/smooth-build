package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.arrayEvaluator;
import static org.smoothbuild.util.Lists.map;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, Location location) {
    super(location);
    this.arrayType = arrayType;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    List<Dag<Evaluator>> elements = evaluators(children, valuesDb, scope);
    ConcreteArrayType actualType = arrayType(elements);
    return new Dag<>(
        arrayEvaluator(actualType, location()),
        convertedElements(actualType.elemType(), elements));
  }

  private static List<Dag<Evaluator>> convertedElements(ConcreteType type,
      List<Dag<Evaluator>> elements) {
    return map(elements, e -> convertIfNeeded(type, e));
  }

  private ConcreteArrayType arrayType(List<Dag<Evaluator>> elements) {
    return (ConcreteArrayType) elements
        .stream()
        .map(e -> (Type) e.elem().type())
        .reduce((a, b) -> a.commonSuperType(b))
        .map(t -> t.increaseCoreDepthBy(1))
        .orElse(arrayType);
  }
}
