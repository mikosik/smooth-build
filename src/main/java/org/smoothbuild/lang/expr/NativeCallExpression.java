package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.type.GenericTypeMap.inferMapping;
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

public class NativeCallExpression extends Expression {
  private final NativeFunction nativeFunction;

  public NativeCallExpression(NativeFunction nativeFunction, List<? extends Expression> arguments,
      Location location) {
    super(arguments, location);
    this.nativeFunction = nativeFunction;
  }

  @Override
  public Evaluator createEvaluator(List<Expression> children, ValuesDb valuesDb,
      Scope<Evaluator> scope) {
    List<Evaluator> arguments = evaluators(children, valuesDb, scope);
    List<Type> parameterTypes = nativeFunction.parameterTypes();
    GenericTypeMap<ConcreteType> mapping =
        inferMapping(parameterTypes, evaluatorTypes(arguments));
    ConcreteType actualResultType = mapping.applyTo(nativeFunction.signature().type());
    return nativeCallEvaluator(
        actualResultType,
        nativeFunction,
        convertedArguments(mapping.applyTo(parameterTypes), arguments),
        location());
  }

  private static List<Evaluator> convertedArguments(
      List<ConcreteType> actualParameterTypes, List<Evaluator> arguments) {
    List<Evaluator> result = new ArrayList<>();
    for (int i = 0; i < arguments.size(); i++) {
      result.add(convertIfNeeded(actualParameterTypes.get(i), arguments.get(i)));
    }
    return result;
  }
}
