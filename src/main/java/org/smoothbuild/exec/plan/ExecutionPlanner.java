package org.smoothbuild.exec.plan;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.cli.console.Log.error;
import static org.smoothbuild.lang.base.define.Location.commandLineLocation;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.cli.console.Reporter;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.expr.ExpressionVisitorException;

public class ExecutionPlanner {
  private final ExpressionToTaskConverterProvider converterProvider;
  private final Reporter reporter;

  @Inject
  public ExecutionPlanner(ExpressionToTaskConverterProvider converterProvider, Reporter reporter) {
    this.converterProvider = converterProvider;
    this.reporter = reporter;
  }

  public List<Task> createPlans(Definitions definitions, List<Value> values) {
    ExpressionToTaskConverter converter = converterProvider.get(definitions);
    return values
        .stream()
        .map(v -> createPlan(v, converter))
        .collect(toList());
  }

  private Task createPlan(Value value, ExpressionToTaskConverter converter) {
    Expression expression = value.createReferenceExpression(commandLineLocation());
    return createPlan(value.name(), expression, converter);
  }

  private Task createPlan(String name, Expression expression, ExpressionToTaskConverter converter) {
    try {
      return expression.visit(converter);
    } catch (ExpressionVisitorException e) {
      reporter.report("Building execution plan for " + name, list(error(e.getMessage())));
      return null;
    }
  }
}
