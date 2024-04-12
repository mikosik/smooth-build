package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.virtualmachine.VirtualMachineConstants.EVALUATE_PREFIX;

import jakarta.inject.Inject;
import java.util.function.Consumer;
import org.smoothbuild.common.concurrent.SoftTerminationExecutor;
import org.smoothbuild.common.function.Consumer0;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class TaskExecutor {
  private final SoftTerminationExecutor executor;
  private final Computer computer;
  private final TaskReporter taskReporter;
  private final Reporter reporter;

  @Inject
  public TaskExecutor(Computer computer, TaskReporter taskReporter, Reporter reporter) {
    this(computer, taskReporter, reporter, Runtime.getRuntime().availableProcessors());
  }

  public TaskExecutor(
      Computer computer, TaskReporter taskReporter, Reporter reporter, int threadCount) {
    this.reporter = reporter;
    this.executor = new SoftTerminationExecutor(threadCount);
    this.computer = computer;
    this.taskReporter = taskReporter;
  }

  public void enqueue(Task task, BTuple input, Consumer<BValue> consumer) {
    enqueue(() -> {
      var resultHandler = new ResultHandler(task, executor, taskReporter, consumer);
      // TODO bytecodeException thrown by compute() should be reported differently with more
      //  details
      computer.compute(task, input, r -> enqueue(() -> resultHandler.accept(r)));
    });
  }

  public <T extends Throwable> void enqueue(Consumer0<T> consumer0) {
    executor.enqueue(() -> {
      try {
        consumer0.accept();
      } catch (Throwable e) {
        reporter.report(report(label(EVALUATE_PREFIX), "", EXECUTION, list(fatal(e))));
        executor.terminate();
      }
    });
  }

  public void terminate() {
    executor.terminate();
  }

  public void awaitTermination() throws InterruptedException {
    executor.awaitTermination();
  }
}
