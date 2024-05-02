package org.smoothbuild.cli.command;

import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.Main.EXIT_CODE_SUCCESS;

import com.google.inject.Key;
import jakarta.inject.Inject;
import org.smoothbuild.cli.report.StatusPrinter;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.task.TaskExecutor;

public class CommandRunner {
  private final TaskExecutor taskExecutor;
  private final StatusPrinter statusPrinter;

  @Inject
  public CommandRunner(TaskExecutor taskExecutor, StatusPrinter statusPrinter) {
    this.taskExecutor = taskExecutor;
    this.statusPrinter = statusPrinter;
  }

  public int run(Class<? extends Task0<Void>> taskClass) {
    var promise = taskExecutor.submit(Key.get(taskClass));
    try {
      taskExecutor.waitUntilIdle();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    statusPrinter.printSummary();
    return promise.toMaybe().map(v -> EXIT_CODE_SUCCESS).getOr(EXIT_CODE_ERROR);
  }
}
