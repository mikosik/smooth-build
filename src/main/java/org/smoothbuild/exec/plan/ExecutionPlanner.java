package org.smoothbuild.exec.plan;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
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

import com.google.common.collect.ImmutableMap;

public class ExecutionPlanner {
  private final ExpressionToTaskConverterProvider converterProvider;

  @Inject
  public ExecutionPlanner(ExpressionToTaskConverterProvider converterProvider) {
    this.converterProvider = converterProvider;
  }

  public ImmutableMap<Value, Task> createPlans(Definitions definitions, List<Value> values) {
    TaskCreator converter = converterProvider.get(definitions);
    return values.stream()
        .collect(toImmutableMap(v -> v, v -> createPlan(v, converter)));
  }

  private Task createPlan(Value value, TaskCreator converter) {
    Expression expression = new ReferenceExpression(
        value.name(), value.type(), commandLineLocation());
    return createPlan(expression, converter);
  }

  private Task createPlan(Expression expression, TaskCreator converter) {
    return converter.taskFor(expression, new Scope<>(Map.of()));
  }
}
