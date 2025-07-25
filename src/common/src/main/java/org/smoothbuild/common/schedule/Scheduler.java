package org.smoothbuild.common.schedule;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.concurrent.Promise.promise;
import static org.smoothbuild.common.concurrent.Promise.runWhenAllAvailable;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.common.schedule.Tasks.taskX;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.MutablePromise;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.dagger.PerCommand;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Reporter;

/**
 * Executes submitted task asynchronously providing result via returned Promise.
 * If submitted task during its execution submits other task then order in which Reports of those
 * tasks are submitted to Reporter is not deterministic.
 * This class is thread-safe.
 */
@PerCommand
public class Scheduler {
  /*
   * Design decisions: Output returned by Task*.execute() contains `Promise<T> result` field
   * instead of `T result` so scheduling tasks (tasks that starts other tasks (scheduled tasks)
   * to calculate result for them) can simply return `Promise` that they received when submitting
   * scheduled task to Scheduler.
   */
  public static final Label LABEL = label(":scheduler");
  private final Reporter reporter;
  private final RunnableScheduler runnableScheduler;

  public Scheduler(Reporter reporter) {
    this(reporter, new VirtualThreadRunnableScheduler());
  }

  @Inject
  public Scheduler(Reporter reporter, RunnableScheduler runnableScheduler) {
    this.reporter = reporter;
    this.runnableScheduler = runnableScheduler;
  }

  // Task0

  public <R> Promise<Maybe<R>> submit(Task0<R> task) {
    return submit(list(), task);
  }

  public <R> Promise<Maybe<R>> submit(
      List<? extends Promise<? extends Maybe<?>>> predecessors, Task0<R> task) {
    var execution = new Execution<>(task);
    return submit(predecessors, execution);
  }

  // Task1

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
    return submit(taskX(LABEL.append(":join"), l -> l), list);
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
        runnableScheduler.submit(execution);
      }
    });
    return execution.resultPromise();
  }

  private static List<Promise<? extends Maybe<?>>> concatenate(
      List<? extends Promise<? extends Maybe<?>>> predecessors,
      List<? extends Promise<? extends Maybe<?>>> args) {
    return List.<Promise<? extends Maybe<?>>>list().addAll(predecessors).addAll(args);
  }

  private class Execution<R> implements Runnable {
    private final MutablePromise<Maybe<R>> result;
    private final Task0<R> task;

    private Execution(Task0<R> task) {
      this.task = task;
      this.result = promise();
    }

    @Override
    public void run() {
      try {
        var output = task.execute();
        reporter.submit(output.report());
        output.result().addConsumer(result);
      } catch (Exception e) {
        var fatal = fatal("Task execution failed with exception:", e);
        reporter.submit(report(LABEL, list(fatal)));
        result.accept(none());
      }
    }

    public Promise<Maybe<R>> resultPromise() {
      return result;
    }
  }
}
