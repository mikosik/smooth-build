package org.smoothbuild.common.plan;

import static org.smoothbuild.common.collect.Maybe.none;

import com.google.inject.Key;
import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.task.TaskExecutor;

public class PlanExecutorWrapper {
  private final TaskExecutor taskExecutor;
  private final PlanExecutor planExecutor;

  @Inject
  public PlanExecutorWrapper(TaskExecutor taskExecutor, PlanExecutor planExecutor) {
    this.taskExecutor = taskExecutor;
    this.planExecutor = planExecutor;
  }

  public <V> Maybe<V> evaluate(Plan<V> plan) {
    var initializerPromise = taskExecutor.submit(Key.get(Initializer.class));
    try {
      taskExecutor.waitUntilIdle();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    if (initializerPromise.toMaybe().isNone()) {
      return none();
    }
    return planExecutor.evaluate(plan);
  }
}
