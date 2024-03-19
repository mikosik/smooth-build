package org.smoothbuild.app.cli.base;

import static org.smoothbuild.app.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.app.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.dag.Dag.apply1;

import com.google.inject.Injector;
import org.smoothbuild.app.report.LogSummaryPrinter;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.dag.DagEvaluator;
import org.smoothbuild.common.log.report.Reporter;

public class ExecuteDag {
  public static Integer executeDag(Injector injector, Dag<String> dag) {
    var dagEvaluator = injector.getInstance(DagEvaluator.class);
    var logSummaryPrinter = injector.getInstance(LogSummaryPrinter.class);
    var reporter = injector.getInstance(Reporter.class);

    var reportResult = apply1(ReportResult.class, dag);

    Maybe<Void> message = dagEvaluator.evaluate(reportResult, reporter);
    logSummaryPrinter.printSummary();
    return message.map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
  }
}
