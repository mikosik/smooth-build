package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.Main.EXIT_CODE_SUCCESS;

import jakarta.inject.Inject;
import org.smoothbuild.cli.report.StatusPrinter;
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

  public <T> Integer execute(Plan<T> plan) {
    Maybe<T> result = planExecutorWrapper.evaluate(plan);
    statusPrinter.printSummary();
    return result.map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
  }
}
