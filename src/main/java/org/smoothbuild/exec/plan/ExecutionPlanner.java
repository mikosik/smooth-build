package org.smoothbuild.exec.plan;

import static com.google.common.collect.ImmutableMap.toImmutableMap;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.base.define.Definitions;
import org.smoothbuild.lang.base.define.Value;

import com.google.common.collect.ImmutableMap;

public class ExecutionPlanner {
  private final ExpressionToTaskConverterProvider converterProvider;

  @Inject
  public ExecutionPlanner(ExpressionToTaskConverterProvider converterProvider) {
    this.converterProvider = converterProvider;
  }

  public ImmutableMap<Value, Task> createPlans(Definitions definitions, List<Value> values) {
    TaskCreator taskCreator = converterProvider.get(definitions);
    return values.stream()
        .collect(toImmutableMap(v -> v, taskCreator::commandLineValueTask));
  }
}
