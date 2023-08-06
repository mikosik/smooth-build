package org.smoothbuild.virtualmachine.evaluate;

import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.execute.SchedulerB;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class EvaluatorB {
  private final Provider<SchedulerB> schedulerProvider;
  private final TaskReporter taskReporter;

  @Inject
  public EvaluatorB(Provider<SchedulerB> schedulerProvider, TaskReporter taskReporter) {
    this.schedulerProvider = schedulerProvider;
    this.taskReporter = taskReporter;
  }

  public Maybe<List<ValueB>> evaluate(List<ExprB> exprs) {
    var schedulerB = schedulerProvider.get();
    var evaluationResults = exprs.map(schedulerB::scheduleExprEvaluation);
    runWhenAllAvailable(evaluationResults, schedulerB::terminate);
    try {
      schedulerB.awaitTermination();
    } catch (InterruptedException e) {
      taskReporter.reportEvaluationException(e);
      return none();
    }
    List<Maybe<ValueB>> map = evaluationResults.map(r -> maybe(r.get()));
    return pullUpMaybe(map);
  }
}
