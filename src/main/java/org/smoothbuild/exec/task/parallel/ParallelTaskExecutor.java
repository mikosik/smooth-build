package org.smoothbuild.exec.task.parallel;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.util.concurrent.Feeder.runWhenAllAvailable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.base.ComputableTask;
import org.smoothbuild.exec.task.base.Computer;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;
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

  public Map<Task, SObject> executeAll(List<Task> tasks) throws InterruptedException {
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

    public Map<Task, SObject> executeAll(List<Task> tasks) throws InterruptedException {
      List<Feeder<SObject>> results = tasks.stream()
          .map(t -> t.startComputation(this))
          .collect(toList());
      runWhenAllAvailable(results, jobExecutor::terminate);

      jobExecutor.awaitTermination();
      return toMap(tasks, results);
    }

    private static HashMap<Task, SObject> toMap(List<Task> tasks, List<Feeder<SObject>> results) {
      HashMap<Task, SObject> result = new HashMap<>();
      Iterator<Feeder<SObject>> it = results.iterator();
      for (Task task : tasks) {
        result.put(task, it.next().value());
      }
      return result;
    }

    public void enqueueComputation(ComputableTask task, Input input, Feeder<SObject> result) {
      jobExecutor.enqueue(() -> {
        try {
          ResultHandler resultHandler = new ResultHandler(task, result, reporter, jobExecutor);
          computer.compute(task, input, resultHandler);
        } catch (Throwable e) {
          reporter.reportComputerException(task, e);
          jobExecutor.terminate();
        }
      });
    }
  }
}
