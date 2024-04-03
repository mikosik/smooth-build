package org.smoothbuild.app.cli.base;

import static org.smoothbuild.app.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.app.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.dag.Dag.apply0;
import static org.smoothbuild.common.dag.Dag.chain;

import com.google.inject.Injector;
import org.smoothbuild.app.report.StatusPrinter;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.dag.DagEvaluator;
import org.smoothbuild.common.dag.Initializator;
import org.smoothbuild.common.log.report.Reporter;

public class ExecuteDag {
  public static Integer executeDagWithInitializables(Injector injector, Dag<Void> dag) {
    var dagWithInitializator = chain(apply0(Initializator.class), dag);
    return executeDag(injector, dagWithInitializator);
  }

  public static Integer executeDag(Injector injector, Dag<Void> dagWithInitializator) {
    var dagEvaluator = injector.getInstance(DagEvaluator.class);
    var logSummaryPrinter = injector.getInstance(StatusPrinter.class);
    var reporter = injector.getInstance(Reporter.class);

    Maybe<Void> message = dagEvaluator.evaluate(dagWithInitializator, reporter);
    logSummaryPrinter.printSummary();
    return message.map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
  }
}
