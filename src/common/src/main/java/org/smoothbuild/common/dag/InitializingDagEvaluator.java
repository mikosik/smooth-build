package org.smoothbuild.common.dag;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;

public class InitializingDagEvaluator {
  private final Initializer initializer;
  private final DagEvaluator dagEvaluator;
  private final Reporter reporter;

  @Inject
  public InitializingDagEvaluator(
      Initializer initializer, DagEvaluator dagEvaluator, Reporter reporter) {
    this.initializer = initializer;
    this.dagEvaluator = dagEvaluator;
    this.reporter = reporter;
  }

  public <V> Maybe<V> evaluate(Dag<V> dag) {
    var result = initializer.apply();
    reporter.report(report(label("initialize"), new Trace<>(), EXECUTION, result.logs()));
    if (result.toMaybe().isNone()) {
      return none();
    }
    return dagEvaluator.evaluate(dag);
  }
}
