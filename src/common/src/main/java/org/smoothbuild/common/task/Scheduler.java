package org.smoothbuild.common.task;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.concurrent.Promise.runWhenAllAvailable;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.task.Tasks.taskX;

import com.google.inject.Injector;
import com.google.inject.Key;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.function.Supplier;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
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
public class Scheduler {
  /*
   * Design decisions: Output returned by Task*.execute() contains `Promise<T> result` field
   * instead of `T result` so scheduling tasks (tasks that starts other tasks (scheduled tasks)
   * to calculate result for them) can simply return `Promise` that they received when submitting
   * scheduled task to Scheduler.
   */
  public static final Label LABEL = label("scheduler");
  private final Injector injector;
  private final Executor executor;
  private final Reporter reporter;

  @Inject
  public Scheduler(Injector injector, Reporter reporter) {
    this(injector, reporter, Runtime.getRuntime().availableProcessors());
  }

  public Scheduler(Injector injector, Reporter reporter, int threadCount) {
    this.injector = injector;
    this.reporter = reporter;
    this.executor = new Executor(threadCount);
  }

  // Task0

  public <R> Promise<Maybe<R>> submit(Class<? extends Task0<R>> task) {
    return submit(Key.get(task));
  }

  public <R> Promise<Maybe<R>> submit(Key<? extends Task0<R>> task) {
    return submit(list(), task);
  }

  public <R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors, Class<? extends Task0<R>> task) {
    return submit(predecessors, Key.get(task));
  }

  public <R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors, Key<? extends Task0<R>> task) {
    return submit(predecessors, injector.getInstance(task));
  }

  public <R> Promise<Maybe<R>> submit(Task0<R> task) {
    return submit(list(), task);
  }

  public <R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors, Task0<R> task) {
    var execution = new Execution<>(task::execute);
    return submit(predecessors, execution);
  }

  // Task1

  public <A1, R> Promise<Maybe<R>> submit(
      Class<? extends Task1<A1, R>> task, Promise<? extends Maybe<? extends A1>> arg1) {
    return submit(Key.get(task), arg1);
  }

  public <A1, R> Promise<Maybe<R>> submit(
      Key<? extends Task1<A1, R>> task, Promise<? extends Maybe<? extends A1>> arg1) {
    return submit(list(), task, arg1);
  }

  public <A1, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Class<? extends Task1<A1, R>> task,
      Promise<? extends Maybe<? extends A1>> arg1) {
    return submit(predecessors, Key.get(task), arg1);
  }

  public <A1, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Key<? extends Task1<A1, R>> task,
      Promise<? extends Maybe<? extends A1>> arg1) {
    return submit(predecessors, injector.getInstance(task), arg1);
  }

  public <A1, R> Promise<Maybe<R>> submit(
      Task1<A1, R> task, Promise<? extends Maybe<? extends A1>> arg1) {
    return submit(list(), task, arg1);
  }

  public <A1, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Task1<A1, R> task,
      Promise<? extends Maybe<? extends A1>> arg1) {
    var execution = new Execution<>(() -> task.execute(arg1.get().get()));
    return submit(predecessors, execution, arg1);
  }

  // Task2

  public <A1, A2, R> Promise<Maybe<R>> submit(
      Class<? extends Task2<A1, A2, R>> task,
      Promise<? extends Maybe<? extends A1>> arg1,
      Promise<? extends Maybe<? extends A2>> arg2) {
    return submit(Key.get(task), arg1, arg2);
  }

  public <A1, A2, R> Promise<Maybe<R>> submit(
      Key<? extends Task2<A1, A2, R>> task,
      Promise<? extends Maybe<? extends A1>> arg1,
      Promise<? extends Maybe<? extends A2>> arg2) {
    return submit(list(), task, arg1, arg2);
  }

  public <A1, A2, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Class<? extends Task2<A1, A2, R>> task,
      Promise<? extends Maybe<? extends A1>> arg1,
      Promise<? extends Maybe<? extends A2>> arg2) {
    return submit(predecessors, Key.get(task), arg1, arg2);
  }

  public <A1, A2, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Key<? extends Task2<A1, A2, R>> task,
      Promise<? extends Maybe<? extends A1>> arg1,
      Promise<? extends Maybe<? extends A2>> arg2) {
    return submit(predecessors, injector.getInstance(task), arg1, arg2);
  }

  public <A1, A2, R> Promise<Maybe<R>> submit(
      Task2<A1, A2, R> task,
      Promise<? extends Maybe<? extends A1>> arg1,
      Promise<? extends Maybe<? extends A2>> arg2) {
    return submit(list(), task, arg1, arg2);
  }

  public <A1, A2, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Task2<A1, A2, R> task,
      Promise<? extends Maybe<? extends A1>> arg1,
      Promise<? extends Maybe<? extends A2>> arg2) {
    var execution =
        new Execution<>(() -> task.execute(arg1.get().get(), arg2.get().get()));
    return submit(predecessors, execution, arg1, arg2);
  }

  // TaskX

  public <A, R> Promise<Maybe<R>> submit(
      Class<? extends TaskX<A, R>> task,
      List<? extends Promise<? extends Maybe<? extends A>>> args) {
    return submit(Key.get(task), args);
  }

  public <A, R> Promise<Maybe<R>> submit(
      Key<? extends TaskX<A, R>> task, List<? extends Promise<? extends Maybe<? extends A>>> args) {
    return submit(list(), task, args);
  }

  public <A, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Class<? extends TaskX<A, R>> task,
      List<? extends Promise<? extends Maybe<? extends A>>> args) {
    return submit(predecessors, Key.get(task), args);
  }

  public <A, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Key<? extends TaskX<A, R>> task,
      List<? extends Promise<? extends Maybe<? extends A>>> args) {
    return submit(predecessors, injector.getInstance(task), args);
  }

  public <A, R> Promise<Maybe<R>> submit(
      TaskX<A, R> task, List<? extends Promise<? extends Maybe<? extends A>>> args) {
    return submit(list(), task, args);
  }

  public <A, R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      TaskX<A, R> task,
      List<? extends Promise<? extends Maybe<? extends A>>> args) {
    var execution = new Execution<>(() -> task.execute(args.map(p -> p.get().get())));
    return submit(predecessors, execution, args);
  }

  // helpers

  public <T> Promise<Maybe<List<T>>> join(
      List<? extends Promise<? extends Maybe<? extends T>>> list) {
    return submit(taskX(LABEL.append("join"), l -> l), list);
  }

  public <A1, R> Task1<List<? extends A1>, List<R>> newParallelTask(
      Class<? extends Task1<A1, R>> task) {
    return newParallelTask(injector.getInstance(task));
  }

  public <A1, R> Task1<List<? extends A1>, List<R>> newParallelTask(Task1<A1, R> task) {
    return new ParallelTask<>(this, task);
  }

  // private

  @SafeVarargs
  private <R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Execution<R> execution,
      Promise<? extends Maybe<?>>... args) {
    return submit(predecessors, execution, list(args));
  }

  private <R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      Execution<R> execution,
      List<? extends Promise<? extends Maybe<?>>> args) {
    var dependencies = concatenate(predecessors, args);
    runWhenAllAvailable(dependencies, () -> {
      if (dependencies.anyMatches(d -> d.get().isNone())) {
        execution.result.accept(none());
      } else {
        executor.submit(execution);
      }
    });
    return execution.resultPromise();
  }

  private static List<Promise<? extends Maybe<?>>> concatenate(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      List<? extends Promise<? extends Maybe<?>>> args) {
    return List.<Promise<? extends Maybe<?>>>list().appendAll(predecessors).appendAll(args);
  }

  private class Execution<R> implements Runnable {
    private final MutablePromise<Maybe<R>> result;
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
        taskResult.result().addConsumer(result);
      } catch (Exception e) {
        var fatal = fatal("Task execution failed with exception:", e);
        reporter.submit(report(LABEL, new Trace(), EXECUTION, list(fatal)));
        result.accept(none());
      }
    }

    public Promise<Maybe<R>> resultPromise() {
      return result;
    }
  }
}
