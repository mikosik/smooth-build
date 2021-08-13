package org.smoothbuild.exec.parallel;

import static org.smoothbuild.util.Maps.mapValues;
import static org.smoothbuild.util.concurrent.Feeders.runWhenAllAvailable;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.db.object.base.Obj;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.compute.AlgorithmTask;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

/**
 * Executes tasks in parallel.
 *
 * This class is thread-safe.
 */
public class ParallelTaskExecutor {
  private final Computer computer;
  private final ExecutionReporter reporter;
  private final int threadCount;

  @Inject
  public ParallelTaskExecutor(Computer computer, ExecutionReporter reporter) {
    this(computer, reporter, Runtime.getRuntime().availableProcessors());
  }

  public ParallelTaskExecutor(Computer computer, ExecutionReporter reporter,
      int threadCount) {
    this.computer = computer;
    this.reporter = reporter;
    this.threadCount = threadCount;
  }

  public Map<Value, Optional<Obj>> executeAll(Map<Value, Task> tasks) throws InterruptedException {
    SoftTerminationExecutor executor = new SoftTerminationExecutor(threadCount);
    return new Worker(computer, reporter, executor).executeAll(tasks);
  }

  public static class Worker {
    private final SoftTerminationExecutor jobExecutor;
    private final Computer computer;
    private final ExecutionReporter reporter;

    @Inject
    public Worker(Computer computer, ExecutionReporter reporter,
        SoftTerminationExecutor jobExecutor) {
      this.jobExecutor = jobExecutor;
      this.computer = computer;
      this.reporter = reporter;
    }

    public ExecutionReporter reporter() {
      return reporter;
    }

    public Map<Value, Optional<Obj>> executeAll(Map<Value, Task> tasks)
        throws InterruptedException {
      var results = mapValues(tasks, task -> task.compute(this));
      runWhenAllAvailable(results.values(), jobExecutor::terminate);

      jobExecutor.awaitTermination();
      return mapValues(results, feeder -> Optional.ofNullable(feeder.get()));
    }

    public void enqueueComputation(AlgorithmTask task, Input input, Consumer<Obj> consumer) {
      jobExecutor.enqueue(() -> {
        try {
          ResultHandler resultHandler = new ResultHandler(task, consumer, reporter, jobExecutor);
          computer.compute(task, input, resultHandler);
        } catch (Throwable e) {
          reporter.reportComputerException(task, e);
          jobExecutor.terminate();
        }
      });
    }
  }
}
