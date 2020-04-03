package org.smoothbuild.exec.task.parallel;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.stream.Collectors.toList;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.util.Collections.toMap;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.inject.Inject;

import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.exec.task.base.ExecutionResult;
import org.smoothbuild.exec.task.base.Result;
import org.smoothbuild.exec.task.base.Task;
import org.smoothbuild.exec.task.base.TaskExecutor;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.SoftTerminationExecutor;
import org.smoothbuild.util.concurrent.ThresholdRunnable;

import com.google.common.collect.ImmutableList;

/**
 * Executes tasks in parallel.
 *
 * This class is thread-safe.
 */
public class ParallelTaskExecutor {
  private final TaskExecutor taskExecutor;
  private final ExecutionReporter reporter;
  private final int threadCount;

  @Inject
  public ParallelTaskExecutor(TaskExecutor taskExecutor, ExecutionReporter reporter) {
    this(taskExecutor, reporter, Runtime.getRuntime().availableProcessors());
  }

  public ParallelTaskExecutor(TaskExecutor taskExecutor, ExecutionReporter reporter,
      int threadCount) {
    this.taskExecutor = taskExecutor;
    this.reporter = reporter;
    this.threadCount = threadCount;
  }

  public Map<Task, Result> executeAll(List<Task> tasks) throws InterruptedException {
    SoftTerminationExecutor executor = new SoftTerminationExecutor(threadCount);
    return new Worker(taskExecutor, reporter, executor).executeAll(tasks);
  }

  private static class Worker {
    private final SoftTerminationExecutor jobExecutor;
    private final TaskExecutor taskExecutor;
    private final ExecutionReporter reporter;

    @Inject
    public Worker(TaskExecutor taskExecutor, ExecutionReporter reporter,
        SoftTerminationExecutor jobExecutor) {
      this.jobExecutor = jobExecutor;
      this.taskExecutor = taskExecutor;
      this.reporter = reporter;
    }

    public Map<Task, Result> executeAll(List<Task> tasks) throws InterruptedException {
      List<Job> jobs = tasks.stream()
          .map(this::job)
          .collect(toList());
      createRootTasksListener(tasks, jobs);

      jobExecutor.awaitTermination();
      return toMap(jobs, Job::task, Job::taskResult);
    }

    private void createRootTasksListener(List<Task> tasks, List<Job> jobs) {
      ThresholdRunnable terminator = new ThresholdRunnable(tasks.size(), jobExecutor::terminate);
      jobs.forEach(job -> job.addValueAvailableListener(terminator));
    }

    private Job job(Task task) {
      return task.name().equals("if")
          ? ifJob(task)
          : normalJob(task);
    }

    private Job normalJob(Task task) {
      Job job = new Job(task);
      ImmutableList<Job> childrenJobs = task.children()
          .stream()
          .map(this::job)
          .collect(toImmutableList());
      ThresholdRunnable enqueuer = new ThresholdRunnable(
          childrenJobs.size(), () -> enqueueExecution(job, toInput(childrenJobs)));
      childrenJobs.forEach(childJob -> childJob.addValueAvailableListener(enqueuer));
      return job;
    }

    private Job ifJob(Task ifTask) {
      Job ifJob = new Job(ifTask);
      Job conditionJob = job(conditionChild(ifTask));
      conditionJob.addValueConsumer(thenOrElseEnqueuer(ifTask, ifJobEnqueuer(ifJob, conditionJob)));
      return ifJob;
    }

    private Consumer<SObject> thenOrElseEnqueuer(Task ifTask, Consumer<SObject> ifJobStarter) {
      return conditionValue -> {
        boolean condition = ((Bool) conditionValue).jValue();
        Task thenOrElseTask = condition ? thenChild(ifTask) : elseChild(ifTask);
        job(thenOrElseTask).addValueConsumer(ifJobStarter);
      };
    }

    private Consumer<SObject> ifJobEnqueuer(Job ifJob, Job conditionJob) {
      return thenOrElseValue -> {
        SObject conditionValue = conditionJob.taskResult().output().value();

        // Only one of then/else values will be used and it will be used twice.
        // This way TaskExecutor can calculate task hash and use it for caching.
        Input input = input(ImmutableList.of(conditionValue, thenOrElseValue, thenOrElseValue));
        enqueueExecution(ifJob, input);
      };
    }

    private void enqueueExecution(Job job, Input input) {
      jobExecutor.enqueue(() -> {
        try {
          Task task = job.task();
          taskExecutor.compute(task.algorithm(), input, executionResultHandler(job),
              task.isComputationCacheable());
        } catch (Throwable e) {
          reporter.report(e);
          jobExecutor.terminate();
        }
      });
    }

    private Consumer<ExecutionResult> executionResultHandler(Job job) {
      Consumer<Result> taskResultConsumer = (Result taskResult) -> {
        reporter.report(job.task(), taskResult);
        if (!taskResult.hasOutputWithValue()) {
          jobExecutor.terminate();
        }
        job.setTaskResult(taskResult);
      };
      Consumer<Throwable> executionExceptionConsumer = (Throwable throwable) -> {
        reporter.report(throwable);
        jobExecutor.terminate();
      };
      return (ExecutionResult result) -> result
          .apply(taskResultConsumer, executionExceptionConsumer);
    }

    private static Input toInput(List<Job> jobs) {
      List<SObject> childValues = jobs
          .stream()
          .map(job -> job.taskResult().output().value())
          .collect(toImmutableList());
      return input(childValues);
    }

    private static Task conditionChild(Task ifTask) {
      return ifTask.children().get(0);
    }

    private static Task thenChild(Task ifTask) {
      return ifTask.children().get(1);
    }

    private static Task elseChild(Task ifTask) {
      return ifTask.children().get(2);
    }
  }
}
