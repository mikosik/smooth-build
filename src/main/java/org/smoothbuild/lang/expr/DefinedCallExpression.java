package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.lang.object.type.GenericTypeMap.inferMapping;
import static org.smoothbuild.task.base.Evaluator.identityEvaluator;

import java.util.List;

import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.lang.object.type.ConcreteType;
import org.smoothbuild.task.base.Evaluator;

public class DefinedCallExpression extends Expression {
  private final DefinedFunction function;

  public DefinedCallExpression(DefinedFunction definedFunction,
      List<? extends Expression> arguments, Location location) {
    super(arguments, location);
    this.function = definedFunction;
  }

  @Override
  public Evaluator createEvaluator(ObjectsDb objectsDb, Scope<Evaluator> scope) {
    List<Evaluator> arguments = childrenEvaluators(objectsDb, scope);
    ConcreteType actualResultType =
        inferMapping(function.parameterTypes(), evaluatorTypes(arguments))
            .applyTo(function.signature().type());
    Evaluator evaluator = function
        .body()
        .createEvaluator(objectsDb, functionScope(arguments))
        .convertIfNeeded(actualResultType);
    return namedEvaluator(actualResultType, function.name(), evaluator);
  }

  private Scope<Evaluator> functionScope(List<Evaluator> arguments) {
    Scope<Evaluator> functionScope = scope();
    for (int i = 0; i < arguments.size(); i++) {
      functionScope.add(function.parameters().get(i).name(), arguments.get(i));
    }
    return functionScope;
  }

  private Evaluator namedEvaluator(ConcreteType type, String name, Evaluator evaluator) {
    return identityEvaluator(type, name, false, evaluator, location());
  }
}
