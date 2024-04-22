package org.smoothbuild.common.schedule;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.concurrent.Promises.runWhenAllAvailable;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.containsFailure;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;
import static org.smoothbuild.common.log.report.Report.report;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import java.util.function.Supplier;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.Executor;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.concurrent.PromisedValue;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.report.Reporter;
import org.smoothbuild.common.log.report.Trace;

public class Scheduler {
  static final Label SCHEDULE_LABEL = label("schedule");
  private final Injector injector;
  private final Executor executor;
  private final Reporter reporter;

  public Scheduler(Reporter reporter) {
    this(Guice.createInjector(), reporter, 4);
  }

  public Scheduler(Injector injector, Reporter reporter, int threadCount) {
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
    var execution = new Execution<>(() -> task.execute(args.map(Supplier::get)));
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
        new Execution<>(() -> injector.getInstance(task).execute(args.map(Supplier::get)));
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
    private final PromisedValue<R> result;
    private final Supplier<Output<R>> taskResultSupplier;

    private Execution(Supplier<Output<R>> taskResultSupplier) {
      this.taskResultSupplier = taskResultSupplier;
      this.result = new PromisedValue<>();
    }

    @Override
    public void run() {
      try {
        var taskResult = taskResultSupplier.get();
        var report = taskResult.report();
        reporter.submit(report);
        if (!containsFailure(report.logs())) {
          result.accept(taskResult.result());
        }
      } catch (Exception e) {
        var message = "Handling task execution failed with exception:\n" + getStackTraceAsString(e);
        reporter.submit(report(SCHEDULE_LABEL, new Trace(), EXECUTION, list(fatal(message))));
      }
    }

    public Promise<R> resultPromise() {
      return result;
    }
  }
}
