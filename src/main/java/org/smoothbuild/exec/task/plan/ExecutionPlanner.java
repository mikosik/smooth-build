package org.smoothbuild.exec.task.plan;

import javax.inject.Inject;
import javax.inject.Provider;

import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.parse.expr.Expression;

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
