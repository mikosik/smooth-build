package org.smoothbuild.vm.parallel;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.job.algorithm.Algorithm;
import org.smoothbuild.vm.job.job.Job;
import org.smoothbuild.vm.job.job.TaskInfo;

/**
 * Executes tasks in parallel.
 *
 * This class is thread-safe.
 */
public class ParallelJobExecutor {
  private final Computer computer;
  private final ExecutionReporter reporter;
  private final int threadCount;

  @Inject
  public ParallelJobExecutor(Computer computer, ExecutionReporter reporter) {
    this(computer, reporter, Runtime.getRuntime().availableProcessors());
  }

  public ParallelJobExecutor(Computer computer, ExecutionReporter reporter, int threadCount) {
    this.computer = computer;
    this.reporter = reporter;
    this.threadCount = threadCount;
  }

  public List<Optional<ValB>> executeAll(List<Job> jobs) throws InterruptedException {
    SoftTerminationExecutor executor = new SoftTerminationExecutor(threadCount);
    return new Worker(computer, reporter, executor).executeAll(jobs);
  }

  public static class Worker {
    private final SoftTerminationExecutor executor;
    private final Computer computer;
    private final ExecutionReporter reporter;

    @Inject
    public Worker(Computer computer, ExecutionReporter reporter, SoftTerminationExecutor executor) {
      this.executor = executor;
      this.computer = computer;
      this.reporter = reporter;
    }

    public ExecutionReporter reporter() {
      return reporter;
    }

    public List<Optional<ValB>> executeAll(List<Job> jobs)
        throws InterruptedException {
      var results = map(jobs, job -> job.schedule(this));
      runWhenAllAvailable(results, executor::terminate);

      executor.awaitTermination();
      return map(results, promise -> Optional.ofNullable(promise.get()));
    }

    public void enqueue(TaskInfo info, Algorithm algorithm, TupleB input, Consumer<ValB> consumer) {
      executor.enqueue(() -> {
        try {
          var resHandler = new ResHandler(info, consumer, reporter, executor);
          computer.compute(algorithm, input, resHandler);
        } catch (Throwable e) {
          reporter.reportComputerException(info, e);
          executor.terminate();
        }
      });
    }
  }
}
