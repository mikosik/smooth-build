package org.smoothbuild.app.cli.base;

import static org.smoothbuild.app.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.app.SmoothConstants.EXIT_CODE_SUCCESS;

import jakarta.inject.Inject;
import org.smoothbuild.app.report.StatusPrinter;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.dag.Dag;
import org.smoothbuild.common.dag.InitializingDagEvaluator;

public class CommandExecutor {
  private final InitializingDagEvaluator initializingDagEvaluator;
  private final StatusPrinter statusPrinter;

  @Inject
  public CommandExecutor(
      InitializingDagEvaluator initializingDagEvaluator, StatusPrinter statusPrinter) {
    this.initializingDagEvaluator = initializingDagEvaluator;
    this.statusPrinter = statusPrinter;
  }

  public Integer execute(Dag<Void> dag) {
    Maybe<Void> message = initializingDagEvaluator.evaluate(dag);
    statusPrinter.printSummary();
    return message.map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
  }
}
