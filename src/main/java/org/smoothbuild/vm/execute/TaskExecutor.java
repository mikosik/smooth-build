package org.smoothbuild.vm.execute;

import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.task.Task;

public class TaskExecutor {
  private final SoftTerminationExecutor executor;
  private final Computer computer;
  private final ExecutionReporter reporter;

  @Inject
  public TaskExecutor(Computer computer, ExecutionReporter reporter) {
    this(computer, reporter, Runtime.getRuntime().availableProcessors());
  }

  public TaskExecutor(Computer computer, ExecutionReporter reporter, int threadCount) {
    this.executor = new SoftTerminationExecutor(threadCount);
    this.computer = computer;
    this.reporter = reporter;
  }

  public void enqueue(Task task, TupleB input, Consumer<ValB> consumer) {
    executor.enqueue(() -> {
      try {
        var resHandler = new ResHandler(task.info(), executor, reporter, consumer);
        computer.compute(task, input, resHandler);
      } catch (Throwable e) {
        reporter.reportComputerException(task.info(), e);
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
