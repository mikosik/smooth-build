package org.smoothbuild.vm.evaluate;

import static org.smoothbuild.common.collect.List.pullUpOption;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.out.log.Log.fatal;

import io.vavr.control.Option;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.smoothbuild.common.collect.List;
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

  public Option<List<ValueB>> evaluate(List<ExprB> exprs) {
    var schedulerB = schedulerProvider.get();
    var evaluationResults = exprs.map(schedulerB::scheduleExprEvaluation);
    runWhenAllAvailable(evaluationResults, schedulerB::terminate);
    try {
      schedulerB.awaitTermination();
    } catch (InterruptedException e) {
      reporter.report(fatal("Evaluation process has been interrupted."));
      return Option.none();
    }
    List<Option<ValueB>> map = evaluationResults.map(r -> Option.of(r.get()));
    return pullUpOption(map);
  }
}
