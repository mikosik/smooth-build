package org.smoothbuild.virtualmachine.evaluate;

import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.execute.BScheduler;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class BEvaluator {
  private final Provider<BScheduler> schedulerProvider;
  private final TaskReporter taskReporter;

  @Inject
  public BEvaluator(Provider<BScheduler> schedulerProvider, TaskReporter taskReporter) {
    this.schedulerProvider = schedulerProvider;
    this.taskReporter = taskReporter;
  }

  public Maybe<List<BValue>> evaluate(List<BExpr> exprs) {
    var scheduler = schedulerProvider.get();
    var evaluationResults = exprs.map(scheduler::scheduleExprEvaluation);
    runWhenAllAvailable(evaluationResults, scheduler::terminate);
    try {
      scheduler.awaitTermination();
    } catch (InterruptedException e) {
      taskReporter.reportEvaluationException(e);
      return none();
    }
    List<Maybe<BValue>> map = evaluationResults.map(r -> maybe(r.get()));
    return pullUpMaybe(map);
  }
}
