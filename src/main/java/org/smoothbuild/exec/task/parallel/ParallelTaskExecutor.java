package org.smoothbuild.exec.task.parallel;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.exec.task.base.ComputableTask;
import org.smoothbuild.exec.task.base.Computer;
import org.smoothbuild.exec.task.base.MaybeComputed;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.util.concurrent.ThresholdRunnable;

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

  public Map<Task, Output> executeAll(List<Task> tasks) throws InterruptedException {
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

    public Map<Task, Output> executeAll(List<Task> tasks) throws InterruptedException {
      List<ResultFeeder> results = tasks.stream()
          .map(t -> t.startComputation(this))
          .collect(toList());
      createRootTasksListener(tasks, results);

      jobExecutor.awaitTermination();
      return toMap(tasks, results);
    }

    private static Map<Task, Output> toMap(List<Task> tasks, List<ResultFeeder> results) {
      HashMap<Task, Output> result = new HashMap<>();
      Iterator<ResultFeeder> it = results.iterator();
      for (Task task : tasks) {
        result.put(task, it.next().output());
      }
      return result;
    }

    private void createRootTasksListener(List<Task> tasks, List<ResultFeeder> results) {
      ThresholdRunnable terminator = new ThresholdRunnable(tasks.size(), jobExecutor::terminate);
      results.forEach(job -> job.addValueAvailableListener(terminator));
    }

    public void enqueueComputation(ComputableTask task, Input input, ResultFeeder result) {
      jobExecutor.enqueue(() -> {
        try {
          computer.compute(task, input, executionResultHandler(result, task));
        } catch (Throwable e) {
          reporter.report(e);
          jobExecutor.terminate();
        }
      });
    }

    private Consumer<MaybeComputed> executionResultHandler(
        ResultFeeder resultFeeder, Task task) {
      return (MaybeComputed maybeComputed) -> {
        if (maybeComputed.hasComputed()) {
          MaybeOutput result = maybeComputed.computed();
          reporter.report(task, maybeComputed, maybeComputed.isFromCache());
          if (!result.hasOutputWithValue()) {
            jobExecutor.terminate();
          }
          resultFeeder.setResult(result);
        } else {
          Throwable throwable = maybeComputed.throwable();
          reporter.report(throwable);
          jobExecutor.terminate();
        }
      };
    }
  }
}
