package org.smoothbuild.common.plan;

import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.init.Initializer;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;

public class PlanExecutorWrapper {
  private final Initializer initializer;
  private final PlanExecutor planExecutor;
  private final Reporter reporter;

  @Inject
  public PlanExecutorWrapper(
      Initializer initializer, PlanExecutor planExecutor, Reporter reporter) {
    this.initializer = initializer;
    this.planExecutor = planExecutor;
    this.reporter = reporter;
  }

  public <V> Maybe<V> evaluate(Plan<V> plan) {
    var result = initializer.apply();
    reporter.submit(report(label("initialize"), new Trace(), EXECUTION, result.logs()));
    if (result.toMaybe().isNone()) {
      return none();
    }
    return planExecutor.evaluate(plan);
  }
}
