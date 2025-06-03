package org.smoothbuild.cli.command.base;

import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.Main.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.report.Report.report;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.report.Reporter;

public class CompletionWaiter {
  private final StatusPrinter statusPrinter;
  private final Reporter reporter;

  @Inject
  public CompletionWaiter(StatusPrinter statusPrinter, Reporter reporter) {
    this.statusPrinter = statusPrinter;
    this.reporter = reporter;
  }

  public int waitForCompletion(Promise<? extends Maybe<?>> commandResult) {
    try {
      Maybe<?> result = commandResult.getBlocking();
      statusPrinter.printSummary();
      return result.isNone() ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
    } catch (InterruptedException e) {
      var fatal = fatal("main thread has been interrupted");
      reporter.submit(report(label(":smooth"), list(fatal)));
      return EXIT_CODE_ERROR;
    }
  }
}
