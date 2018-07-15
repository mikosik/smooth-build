package org.smoothbuild.lang.expr;

import static org.smoothbuild.lang.base.Scope.scope;
import static org.smoothbuild.task.base.Evaluator.callEvaluator;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.base.DefinedFunction;
import org.smoothbuild.lang.base.Scope;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.util.Dag;

public class CallExpression extends Expression {
  private final DefinedFunction function;

  public CallExpression(DefinedFunction definedFunction, Location location) {
    super(definedFunction.type(), location);
    this.function = definedFunction;
  }

  @Override
  public Dag<Evaluator> createEvaluator(List<Dag<Expression>> children, ValuesDb valuesDb,
      Scope<Dag<Evaluator>> scope) {
    Scope<Dag<Evaluator>> functionScope = scope();
    for (int i = 0; i < children.size(); i++) {
      Dag<Expression> dependency = children.get(i);
      Dag<Evaluator> evaluator = dependency.elem().createEvaluator(
          dependency.children(), valuesDb, scope);
      functionScope.add(function.parameters().get(i).name(), evaluator);
    }
    return new Dag<>(callEvaluator(function, location()),
        createChildrenEvaluators(list(function.definition()), valuesDb, functionScope));
  }
}
