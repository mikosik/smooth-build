package org.smoothbuild.exec.plan;

import javax.inject.Inject;

import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.parse.Definitions;

public class ExecutionPlanner {
  private final ExpressionToTaskConverterProvider converterProvider;

  @Inject
  public ExecutionPlanner(ExpressionToTaskConverterProvider converterProvider) {
    this.converterProvider = converterProvider;
  }

  public Task createPlan(Definitions definitions, Expression agrlessCallExpression) {
    ExpressionToTaskConverter converter = converterProvider.get(definitions);
    return agrlessCallExpression.visit(converter);
  }
}
