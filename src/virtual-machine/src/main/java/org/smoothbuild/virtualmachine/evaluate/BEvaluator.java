package org.smoothbuild.virtualmachine.evaluate;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.pullUpMaybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.virtualmachine.VmConstants.VM_SCHEDULE;

import jakarta.inject.Inject;
import jakarta.inject.Provider;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.execute.Vm;

public class BEvaluator {
  private final Provider<Vm> evaluatorProvider;
  private final Reporter reporter;

  @Inject
  public BEvaluator(Provider<Vm> evaluatorProvider, Reporter reporter) {
    this.evaluatorProvider = evaluatorProvider;
    this.reporter = reporter;
  }

  public Maybe<List<BValue>> evaluate(List<BExpr> exprs) {
    var evaluator = evaluatorProvider.get();
    var evaluationResults = exprs.map(evaluator::evaluate);
    try {
      evaluator.awaitTermination();
    } catch (InterruptedException e) {
      var fatal = fatal("Waiting for evaluation has been interrupted:", e);
      var report = report(VM_SCHEDULE, new Trace(), EXECUTION, list(fatal));
      reporter.submit(report);
      return none();
    }
    List<Maybe<BValue>> map = evaluationResults.map(Promise::toMaybe);
    return pullUpMaybe(map);
  }
}
