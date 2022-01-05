package org.smoothbuild.vm.parallel;

import static org.smoothbuild.util.collect.Maps.mapValues;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.vm.compute.Computer;
import org.smoothbuild.vm.job.algorithm.Algorithm;
import org.smoothbuild.vm.job.algorithm.Input;
import org.smoothbuild.vm.job.job.Job;
import org.smoothbuild.vm.job.job.JobInfo;

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

  public ParallelJobExecutor(Computer computer, ExecutionReporter reporter,
      int threadCount) {
    this.computer = computer;
    this.reporter = reporter;
    this.threadCount = threadCount;
  }

  public <K> Map<K, Optional<ObjB>> executeAll(Map<K, Job> jobs) throws InterruptedException {
    SoftTerminationExecutor executor = new SoftTerminationExecutor(threadCount);
    return new Worker(computer, reporter, executor).executeAll(jobs);
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

    public <K> Map<K, Optional<ObjB>> executeAll(Map<K, Job> jobs)
        throws InterruptedException {
      var results = mapValues(jobs, job -> job.schedule(this));
      runWhenAllAvailable(results.values(), jobExecutor::terminate);

      jobExecutor.awaitTermination();
      return mapValues(results, promise -> Optional.ofNullable(promise.get()));
    }

    public void enqueue(JobInfo info, Algorithm algorithm, List<Promise<ValB>> deps,
        Consumer<ValB> consumer) {
      jobExecutor.enqueue(() -> {
        try {
          var resultHandler = new ResHandler(info, consumer, reporter, jobExecutor);
          Input input = Input.fromPromises(deps);
          computer.compute(algorithm, input, resultHandler);
        } catch (Throwable e) {
          reporter.reportComputerException(info, e);
          jobExecutor.terminate();
        }
      });
    }
  }
}
