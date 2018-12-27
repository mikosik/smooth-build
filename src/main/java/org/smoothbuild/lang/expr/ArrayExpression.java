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

public class ArrayExpression extends Expression {
  private final ArrayType arrayType;

  public ArrayExpression(ArrayType arrayType, List<? extends Expression> elements,
      Location location) {
    super(elements, location);
    this.arrayType = arrayType;
  }

  @Override
  public Evaluator createEvaluator(ValuesDb valuesDb, Scope<Evaluator> scope) {
    List<Evaluator> elements = childrenEvaluators(valuesDb, scope);
    ConcreteArrayType actualType = arrayType(elements);
    return arrayEvaluator(
        actualType,
        convertedElements(actualType.elemType(), elements),
        location());
  }

  private static List<Evaluator> convertedElements(ConcreteType type, List<Evaluator> elements) {
    return map(elements, e -> e.convertIfNeeded(type));
  }

  private ConcreteArrayType arrayType(List<Evaluator> elements) {
    return (ConcreteArrayType) elements
        .stream()
        .map(e -> (Type) e.type())
        .reduce(Type::commonSuperType)
        .map(t -> t.changeCoreDepthBy(1))
        .orElse(arrayType);
  }
}
