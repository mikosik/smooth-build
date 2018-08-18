package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.task.base.Evaluator.identityEvaluator;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.type.GenericTypeMap;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class DefinedCallExpression extends Expression {
  private final DefinedFunction function;

  public DefinedCallExpression(DefinedFunction definedFunction, Location location) {
    super(location);
    this.function = definedFunction;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    List<Dag<Evaluator>> arguments = evaluators(children, valuesDb, scope);
    GenericTypeMap<ConcreteType> mapping =
        GenericTypeMap.inferFrom(function.parameterTypes(), evaluatorTypes(arguments));
    ConcreteType actualResultType = mapping.applyTo(function.signature().type());
    Dag<Evaluator> evaluator = convertIfNeeded(
        actualResultType, evaluator(function.body(), valuesDb, functionScope(arguments)));
    return namedEvaluator(actualResultType, function.name(), evaluator);
  }

  private Scope<Dag<Evaluator>> functionScope(List<Dag<Evaluator>> arguments) {
    Scope<Dag<Evaluator>> functionScope = scope();
    for (int i = 0; i < arguments.size(); i++) {
      functionScope.add(function.parameters().get(i).name(), arguments.get(i));
    }
    return functionScope;
  }

  private Dag<Evaluator> namedEvaluator(ConcreteType type, String name, Dag<Evaluator> evaluator) {
    return new Dag<>(
        identityEvaluator(type, name, false, location()),
        list(evaluator));
  }
}
