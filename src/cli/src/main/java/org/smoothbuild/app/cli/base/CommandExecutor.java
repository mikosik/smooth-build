package org.smoothbuild.app.cli.base;

import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.Main.EXIT_CODE_SUCCESS;

import jakarta.inject.Inject;
import org.smoothbuild.app.report.StatusPrinter;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.plan.Plan;
import org.smoothbuild.common.plan.PlanExecutorWrapper;

public class CommandExecutor {
  private final PlanExecutorWrapper planExecutorWrapper;
  private final StatusPrinter statusPrinter;

  @Inject
  public CommandExecutor(PlanExecutorWrapper planExecutorWrapper, StatusPrinter statusPrinter) {
    this.planExecutorWrapper = planExecutorWrapper;
    this.statusPrinter = statusPrinter;
  }

  public Integer execute(Plan<Void> plan) {
    Maybe<Void> result = planExecutorWrapper.evaluate(plan);
    statusPrinter.printSummary();
    return result.map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
  }
}
