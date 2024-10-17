package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.Main.EXIT_CODE_SUCCESS;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;

import jakarta.inject.Inject;
import org.smoothbuild.cli.report.StatusPrinter;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.common.task.TaskExecutor;

public class CommandCompleter {
  private final TaskExecutor taskExecutor;
  private final StatusPrinter statusPrinter;
  private final Reporter reporter;

  @Inject
  public CommandCompleter(
      TaskExecutor taskExecutor, StatusPrinter statusPrinter, Reporter reporter) {
    this.taskExecutor = taskExecutor;
    this.statusPrinter = statusPrinter;
    this.reporter = reporter;
  }

  public int waitForCompletion() {
    try {
      taskExecutor.waitUntilIdle();
    } catch (InterruptedException e) {
      var fatal = fatal("taskExecutor has been interrupted");
      reporter.submit(report(label("smooth", "executor"), new Trace(), EXECUTION, list(fatal)));
      return EXIT_CODE_ERROR;
    }
    var hasFailures = statusPrinter.printSummary();
    return hasFailures ? EXIT_CODE_ERROR : EXIT_CODE_SUCCESS;
  }
}
