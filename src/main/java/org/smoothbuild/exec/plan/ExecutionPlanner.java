package org.smoothbuild.exec.plan;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.lang.base.define.Location.commandLineLocation;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ReferenceExpression;
import org.smoothbuild.util.Scope;

public class ExecutionPlanner {
  private final ExpressionToTaskConverterProvider converterProvider;

  @Inject
  public ExecutionPlanner(ExpressionToTaskConverterProvider converterProvider) {
    this.converterProvider = converterProvider;
  }

  public List<Task> createPlans(Definitions definitions, List<Value> values) {
    ExpressionToTaskConverter converter = converterProvider.get(definitions);
    return values
        .stream()
        .map(v -> createPlan(v, converter))
        .collect(toList());
  }

  private Task createPlan(Value value, ExpressionToTaskConverter converter) {
    Expression expression = new ReferenceExpression(
        value.name(), value.type(), commandLineLocation());
    return createPlan(expression, converter);
  }

  private Task createPlan(Expression expression, ExpressionToTaskConverter converter) {
    return expression.visit(new Scope<>(Map.of()), converter);
  }
}
