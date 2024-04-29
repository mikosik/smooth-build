package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;

import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Executor;
import org.smoothbuild.common.concurrent.MutablePromise;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;

/**
 * Executes submitted task asynchronously providing result via returned Promise.
 * If submitted task during its execution submits other task then order in which Reports of those
 * tasks are submitted to Reporter is not deterministic.
 * This class is thread-safe.
 */
@Singleton
public class TaskExecutor {
  /*
   * Design decisions: Output returned by Task*.execute() contains `Promise<T> result` field
   * instead of `T result` so scheduling tasks (tasks that starts other tasks (scheduled tasks)
   * to calculate result for them) can simply return `Promise` that they received when submitting
   * scheduled task to TaskExecutor.
   */
  public static final Label EXECUTE_LABEL = label("execute");
  private final Injector injector;
  private final Executor executor;
  private final Reporter reporter;

  @Inject
  public TaskExecutor(Injector injector, Reporter reporter) {
    this(injector, reporter, Runtime.getRuntime().availableProcessors());
  }

  public TaskExecutor(Injector injector, Reporter reporter, int threadCount) {
    this.injector = injector;
    this.reporter = reporter;
    this.executor = new Executor(threadCount);
  }

  public <R> Promise<R> submit(Task0<R> task) {
    return submit(task, list());
  }

  public <R> Promise<R> submit(Task0<R> task, List<? extends Promise<?>> predecessors) {
    var execution = new Execution<>(task::execute);
    return submit(predecessors, execution);
  }

  public <R> Promise<R> submit(Key<? extends Task0<R>> task) {
    return submit(list(), task);
  }

  public <R> Promise<R> submit(
      List<? extends Promise<?>> predecessors, Key<? extends Task0<R>> task) {
    var execution = new Execution<>(() -> injector.getInstance(task).execute());
    return submit(predecessors, execution);
  }

  public <R, A1> Promise<R> submit(Task1<R, A1> task, Promise<A1> arg1) {
    return submit(list(), task, arg1);
  }

  public <R, A1> Promise<R> submit(
      List<? extends Promise<?>> predecessors, Task1<R, A1> task, Promise<A1> arg1) {
    var execution = new Execution<>(() -> task.execute(arg1.get()));
    return submit(predecessors, execution, arg1);
  }

  public <R, A1> Promise<R> submit(Key<? extends Task1<R, A1>> task, Promise<A1> arg1) {
    return submit(list(), task, arg1);
  }

  public <R, A1> Promise<R> submit(
      List<? extends Promise<?>> predecessors, Key<? extends Task1<R, A1>> task, Promise<A1> arg1) {
    var execution = new Execution<>(() -> injector.getInstance(task).execute(arg1.get()));
    return submit(predecessors, execution, arg1);
  }

  public <R, A1, A2> Promise<R> submit(Task2<R, A1, A2> task, Promise<A1> arg1, Promise<A2> arg2) {
    return submit(list(), task, arg1, arg2);
  }

  public <R, A1, A2> Promise<R> submit(
      List<? extends Promise<?>> predecessors,
      Task2<R, A1, A2> task,
      Promise<A1> arg1,
      Promise<A2> arg2) {
    var execution = new Execution<>(() -> task.execute(arg1.get(), arg2.get()));
    return submit(predecessors, execution, arg1, arg2);
  }

  public <R, A1, A2> Promise<R> submit(
      Key<? extends Task2<R, A1, A2>> task, Promise<A1> arg1, Promise<A2> arg2) {
    return submit(list(), task, arg1, arg2);
  }

  public <R, A1, A2> Promise<R> submit(
      List<? extends Promise<?>> predecessors,
      Key<? extends Task2<R, A1, A2>> task,
      Promise<A1> arg1,
      Promise<A2> arg2) {
    var execution =
        new Execution<>(() -> injector.getInstance(task).execute(arg1.get(), arg2.get()));
    return submit(predecessors, execution, arg1, arg2);
  }

  public <R, A> Promise<R> submit(TaskX<R, A> task, List<? extends Promise<A>> args) {
    return submit(list(), task, args);
  }

  public <R, A> Promise<R> submit(
      List<? extends Promise<?>> predecessors, TaskX<R, A> task, List<? extends Promise<A>> args) {
    var execution = new Execution<>(() -> task.execute(args.map(Promise::get)));
    return submit(predecessors, execution, args);
  }

  public <R, A> Promise<R> submit(
      Key<? extends TaskX<R, A>> task, List<? extends Promise<A>> args) {
    return submit(list(), task, args);
  }

  public <R, A> Promise<R> submit(
      List<? extends Promise<?>> predecessors,
      Key<? extends TaskX<R, A>> task,
      List<? extends Promise<A>> args) {
    var execution =
        new Execution<>(() -> injector.getInstance(task).execute(args.map(Promise::get)));
    return submit(predecessors, execution, args);
  }

  private <R> Promise<R> submit(
      List<? extends Promise<?>> predecessors, Execution<R> execution, Promise<?>... args) {
    return submit(predecessors, execution, list(args));
  }

  private <R> Promise<R> submit(
      List<? extends Promise<?>> predecessors,
      Execution<R> execution,
      List<? extends Promise<?>> args) {
    runWhenAllAvailable(concatenate(predecessors, args), () -> executor.submit(execution));
    return execution.resultPromise();
  }

  private static List<Promise<?>> concatenate(
      List<? extends Promise<?>> predecessors, List<? extends Promise<?>> args) {
    // Cast is safe because List is immutable.
    @SuppressWarnings("unchecked")
    var cast = (List<Promise<?>>) predecessors;
    return cast.appendAll(args);
  }

  public void waitUntilIdle() throws InterruptedException {
    executor.waitUntilIdle();
  }

  private class Execution<R> implements Runnable {
    private final MutablePromise<R> result;
    private final Supplier<Output<R>> taskResultSupplier;

    private Execution(Supplier<Output<R>> taskResultSupplier) {
      this.taskResultSupplier = taskResultSupplier;
      this.result = promise();
    }

    @Override
    public void run() {
      try {
        var taskResult = taskResultSupplier.get();
        var report = taskResult.report();
        reporter.submit(report);
        if (!containsFailure(report.logs())) {
          taskResult.result().addConsumer(result);
        }
      } catch (Exception e) {
        var fatal = fatal("Task execution failed with exception:", e);
        reporter.submit(report(EXECUTE_LABEL, new Trace(), EXECUTION, list(fatal)));
      }
    }

    public Promise<R> resultPromise() {
      return result;
    }
  }
}
