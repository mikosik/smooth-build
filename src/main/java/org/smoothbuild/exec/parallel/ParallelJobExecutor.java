package org.smoothbuild.exec.parallel;

import static org.smoothbuild.util.Maps.mapValues;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.exec.algorithm.Algorithm;
import org.smoothbuild.exec.base.Input;
import org.smoothbuild.exec.compute.Computer;
import org.smoothbuild.exec.job.Job;
import org.smoothbuild.exec.job.TaskInfo;
import org.smoothbuild.lang.base.define.Value;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;

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

  public Map<Value, Optional<Obj>> executeAll(Map<Value, Job> jobs) throws InterruptedException {
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

    public Map<Value, Optional<Obj>> executeAll(Map<Value, Job> jobs)
        throws InterruptedException {
      var results = mapValues(jobs, job -> job.schedule(this));
      runWhenAllAvailable(results.values(), jobExecutor::terminate);

      jobExecutor.awaitTermination();
      return mapValues(results, promise -> Optional.ofNullable(promise.get()));
    }

    public void enqueue(TaskInfo info, Algorithm algorithm, List<Promise<Val>> dependencies,
        Consumer<Val> consumer) {
      jobExecutor.enqueue(() -> {
        try {
          var resultHandler = new ResultHandler(info, consumer, reporter, jobExecutor);
          Input input = Input.fromPromises(dependencies);
          computer.compute(algorithm, input, resultHandler);
        } catch (Throwable e) {
          reporter.reportComputerException(info, e);
          jobExecutor.terminate();
        }
      });
    }
  }
}