package org.smoothbuild.exec.parallel;

import static java.util.stream.Collectors.toList;
import static org.smoothbuild.util.concurrent.Feeders.runWhenAllAvailable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.compute.ComputableTask;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.util.concurrent.Feeder;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

import com.google.common.collect.Streams;

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

  public Map<Task, Record> executeAll(Iterable<Task> tasks) throws InterruptedException {
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

    public Map<Task, Record> executeAll(Iterable<Task> tasks) throws InterruptedException {
      List<Feeder<Record>> results = Streams.stream(tasks)
          .map(t -> t.startComputation(this))
          .collect(toList());
      runWhenAllAvailable(results, jobExecutor::terminate);

      jobExecutor.awaitTermination();
      return toMap(tasks, results);
    }

    private static HashMap<Task, Record> toMap(
        Iterable<Task> tasks, List<Feeder<Record>> results) {
      HashMap<Task, Record> result = new HashMap<>();
      Iterator<Feeder<Record>> it = results.iterator();
      for (Task task : tasks) {
        result.put(task, it.next().get());
      }
      return result;
    }

    public void enqueueComputation(ComputableTask task, Input input, Consumer<Record> consumer) {
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
