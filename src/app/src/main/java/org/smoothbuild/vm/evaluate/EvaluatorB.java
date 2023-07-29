package org.smoothbuild.vm.evaluate;

import static org.smoothbuild.common.collect.Lists.map;
import static org.smoothbuild.common.collect.Optionals.pullUp;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;

import java.util.Optional;

import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.execute.SchedulerB;

import com.google.common.collect.ImmutableList;

import jakarta.inject.Inject;
import jakarta.inject.Provider;

public class EvaluatorB {
  private final Provider<SchedulerB> schedulerProvider;

  @Inject
  public EvaluatorB(Provider<SchedulerB> schedulerProvider) {
    this.schedulerProvider = schedulerProvider;
  }

  public Optional<ImmutableList<ValueB>> evaluate(ImmutableList<ExprB> exprs)
      throws InterruptedException {
    var executorB = schedulerProvider.get();
    var evaluationResults = map(exprs, executorB::scheduleExprEvaluation);
    runWhenAllAvailable(evaluationResults, executorB::terminate);
    executorB.awaitTermination();
    return pullUp(map(evaluationResults, r -> Optional.ofNullable(r.get())));
  }
}
