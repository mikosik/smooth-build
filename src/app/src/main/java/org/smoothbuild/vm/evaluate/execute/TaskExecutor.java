package org.smoothbuild.vm.evaluate.execute;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Log.fatal;

import jakarta.inject.Inject;
import java.util.function.Consumer;
import org.smoothbuild.common.concurrent.SoftTerminationExecutor;
import org.smoothbuild.common.function.Consumer0;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Computer;
import org.smoothbuild.vm.evaluate.task.Task;

public class TaskExecutor {
  private final SoftTerminationExecutor executor;
  private final Computer computer;
  private final Reporter reporter;
  private final TaskReporter taskReporter;

  @Inject
  public TaskExecutor(Computer computer, Reporter reporter, TaskReporter taskReporter) {
    this(computer, reporter, taskReporter, Runtime.getRuntime().availableProcessors());
  }

  public TaskExecutor(
      Computer computer, Reporter reporter, TaskReporter taskReporter, int threadCount) {
    this.executor = new SoftTerminationExecutor(threadCount);
    this.computer = computer;
    this.reporter = reporter;
    this.taskReporter = taskReporter;
  }

  public void enqueue(Task task, TupleB input, Consumer<ValueB> consumer) {
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
        reportComputerException(e);
        executor.terminate();
      }
    });
  }

  private void reportComputerException(Throwable throwable) {
    reporter.report(
        "Internal smooth error",
        list(fatal("Computation failed with: " + getStackTraceAsString(throwable))));
  }

  public void terminate() {
    executor.terminate();
  }

  public void awaitTermination() throws InterruptedException {
    executor.awaitTermination();
  }
}
