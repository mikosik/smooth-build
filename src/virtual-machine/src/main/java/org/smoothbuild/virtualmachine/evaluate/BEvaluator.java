package org.smoothbuild.virtualmachine.evaluate;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.virtualmachine.VirtualMachineConstants.EVALUATE_PREFIX;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.execute.BScheduler;

public class BEvaluator {
  private final Provider<BScheduler> schedulerProvider;
  private final Reporter reporter;

  @Inject
  public BEvaluator(Provider<BScheduler> schedulerProvider, Reporter reporter) {
    this.schedulerProvider = schedulerProvider;
    this.reporter = reporter;
  }

  public Maybe<List<BValue>> evaluate(List<BExpr> exprs) {
    var scheduler = schedulerProvider.get();
    var evaluationResults = exprs.map(scheduler::scheduleExprEvaluation);
    runWhenAllAvailable(evaluationResults, scheduler::terminate);
    try {
      scheduler.awaitTermination();
    } catch (InterruptedException e) {
      var report = report(label(EVALUATE_PREFIX), new Trace(), EXECUTION, list(fatal(e)));
      reporter.report(report);
      return none();
    }
    List<Maybe<BValue>> map = evaluationResults.map(r -> maybe(r.get()));
    return pullUpMaybe(map);
  }
}
