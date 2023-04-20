package org.smoothbuild.vm.evaluate.job;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import java.util.function.Consumer;

import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.task.Task;

import com.google.common.collect.ImmutableList;

public abstract class Job {
  private volatile Promise<ValueB> promise;
  private final ExprB exprB;
  private final JobCreator jobCreator;

  public Job(ExprB exprB, JobCreator jobCreator) {
    this.exprB = exprB;
    this.jobCreator = jobCreator;
  }

  public ExprB exprB() {
    return exprB;
  }

  protected JobCreator jobCreator() {
    return jobCreator;
  }

  public final Promise<ValueB> evaluate(ExecutionContext context) {
    // Double-checked locking.
    Promise<ValueB> localPromise = promise;
    if (localPromise != null) {
      return localPromise;
    }
    synchronized (this) {
      localPromise = promise;
      if (localPromise == null) {
        var newPromise = new PromisedValue<ValueB>();
        promise = localPromise = newPromise;
        context.taskExecutor().enqueue(() -> evaluateImpl(context, newPromise));
      }
      return localPromise;
    }
  }

  protected abstract void evaluateImpl(ExecutionContext context, Consumer<ValueB> result);

  protected void evaluateTransitively(ExecutionContext context,
      Task task, ImmutableList<ExprB> deps, Consumer<ValueB> result) {
    var depJs = map(deps, jobCreator::jobFor);
    var depResults = map(depJs, j -> j.evaluate(context));
    runWhenAllAvailable(depResults,
        () -> context.taskExecutor().enqueue(task, toInput(context, depResults), result));
  }

  private TupleB toInput(ExecutionContext context, ImmutableList<Promise<ValueB>> depResults) {
    return context.bytecodeF().tuple(map(depResults, Promise::get));
  }
}
