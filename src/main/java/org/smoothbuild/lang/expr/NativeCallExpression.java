package org.smoothbuild.lang.expr;

import static org.smoothbuild.task.base.Evaluator.nativeCallEvaluator;

import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.NativeFunction;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.GenericTypeMap;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class NativeCallExpression extends Expression {
  private final NativeFunction nativeFunction;

  public NativeCallExpression(Type type, NativeFunction nativeFunction, Location location) {
    super(type, location);
    this.nativeFunction = nativeFunction;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    List<Dag<Evaluator>> arguments = evaluators(children, valuesDb, scope);
    List<Type> parameterTypes = parameterTypes(nativeFunction);
    GenericTypeMap<ConcreteType> mapping =
        GenericTypeMap.inferFrom(parameterTypes, evaluatorTypes(arguments));
    ConcreteType actualResultType = mapping.applyTo(nativeFunction.signature().type());
    return new Dag<>(
        nativeCallEvaluator(actualResultType, nativeFunction, location()),
        convertedArguments(mapping.applyTo(parameterTypes), arguments));
  }

  private static List<Dag<Evaluator>> convertedArguments(
      List<ConcreteType> actualParameterTypes, List<Dag<Evaluator>> arguments) {
    List<Dag<Evaluator>> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      result.add(convertIfNeeded(actualParameterTypes.get(i), arguments.get(i)));
    }
    return result;
  }
}
