package org.smoothbuild.common.dag;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.init.Initializator;
import org.smoothbuild.common.log.report.Reporter;

public class InitializingDagEvaluator {
  private final Initializator initializator;
  private final DagEvaluator dagEvaluator;
  private final Reporter reporter;

  @Inject
  public InitializingDagEvaluator(
      Initializator initializator, DagEvaluator dagEvaluator, Reporter reporter) {
    this.initializator = initializator;
    this.dagEvaluator = dagEvaluator;
    this.reporter = reporter;
  }

  public <V> Maybe<V> evaluate(Dag<V> dag) {
    var result = initializator.apply();
    reporter.report(report(label("initialize"), "", EXECUTION, result.logs()));
    if (result.toMaybe().isNone()) {
      return none();
    }
    return dagEvaluator.evaluate(dag);
  }
}
