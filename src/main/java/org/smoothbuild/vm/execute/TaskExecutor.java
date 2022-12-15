package org.smoothbuild.vm.execute;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.out.log.Log.fatal;
import static org.smoothbuild.util.collect.Lists.list;

import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.util.function.ThrowingRunnable;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.task.Task;

public class TaskExecutor {
  private final SoftTerminationExecutor executor;
  private final Computer computer;
  private final Reporter reporter;
  private final TaskReporter taskReporter;

  @Inject
  public TaskExecutor(Computer computer, Reporter reporter, TaskReporter taskReporter) {
    this(computer, reporter, taskReporter, Runtime.getRuntime().availableProcessors());
  }

  public TaskExecutor(Computer computer, Reporter reporter, TaskReporter taskReporter,
      int threadCount) {
    this.executor = new SoftTerminationExecutor(threadCount);
    this.computer = computer;
    this.reporter = reporter;
    this.taskReporter = taskReporter;
  }

  public void enqueue(Task task, TupleB input, Consumer<ValueB> consumer) {
    enqueue(() -> {
      var resHandler = new ResHandler(task, executor, taskReporter, consumer);
      computer.compute(task, input, resHandler);
    });
  }

  public void enqueue(ThrowingRunnable throwingRunnable) {
    executor.enqueue(() -> {
      try {
        throwingRunnable.run();
      } catch (Throwable e) {
        reportComputerException(e);
        executor.terminate();
      }
    });
  }

  private void reportComputerException(Throwable throwable) {
    reporter.report("Internal smooth error",
        list(fatal("Computation failed with: " + getStackTraceAsString(throwable))));
  }

  public void terminate() {
    executor.terminate();
  }

  public void awaitTermination() throws InterruptedException {
    executor.awaitTermination();
  }
}
