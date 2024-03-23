package org.smoothbuild.virtualmachine.evaluate.execute;

import jakarta.inject.Inject;
import java.util.function.Consumer;
import org.smoothbuild.common.concurrent.SoftTerminationExecutor;
import org.smoothbuild.common.function.Consumer0;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Computer;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class TaskExecutor {
  private final SoftTerminationExecutor executor;
  private final Computer computer;
  private final TaskReporter taskReporter;

  @Inject
  public TaskExecutor(Computer computer, TaskReporter taskReporter) {
    this(computer, taskReporter, Runtime.getRuntime().availableProcessors());
  }

  public TaskExecutor(Computer computer, TaskReporter taskReporter, int threadCount) {
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
        taskReporter.reportEvaluationException(e);
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
