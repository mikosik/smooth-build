package org.smoothbuild.exec.plan;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.expr.Expression;

public class ExecutionPlanner {
  private final Provider<ExpressionToTaskConverter> converterProvider;

  @Inject
  public ExecutionPlanner(Provider<ExpressionToTaskConverter> converterProvider) {
    this.converterProvider = converterProvider;
  }

  public Task createPlan(Expression agrlessCallExpression) {
    ExpressionToTaskConverter converter = converterProvider.get();
    return agrlessCallExpression.visit(converter);
  }
}
