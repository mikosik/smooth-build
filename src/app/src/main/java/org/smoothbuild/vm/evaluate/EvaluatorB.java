package org.smoothbuild.vm.evaluate;

import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.common.log.Log.fatal;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.execute.SchedulerB;

public class EvaluatorB {
  private final Provider<SchedulerB> schedulerProvider;
  private final Reporter reporter;

  @Inject
  public EvaluatorB(Provider<SchedulerB> schedulerProvider, Reporter reporter) {
    this.schedulerProvider = schedulerProvider;
    this.reporter = reporter;
  }

  public Maybe<List<ValueB>> evaluate(List<ExprB> exprs) {
    var schedulerB = schedulerProvider.get();
    var evaluationResults = exprs.map(schedulerB::scheduleExprEvaluation);
    runWhenAllAvailable(evaluationResults, schedulerB::terminate);
    try {
      schedulerB.awaitTermination();
    } catch (InterruptedException e) {
      reporter.report(fatal("Evaluation process has been interrupted."));
      return none();
    }
    List<Maybe<ValueB>> map = evaluationResults.map(r -> maybe(r.get()));
    return pullUpMaybe(map);
  }
}
