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
  private final ExecutionContext context;

  public Job(ExprB exprB, ExecutionContext context) {
    this.exprB = exprB;
    this.context = context;
  }

  public ExprB exprB() {
    return exprB;
  }

  protected ExecutionContext context() {
    return context;
  }

  public final Promise<ValueB> evaluate() {
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
        context().taskExecutor().enqueue(() -> evaluateImpl(newPromise));
      }
      return localPromise;
    }
  }

  protected abstract void evaluateImpl(Consumer<ValueB> result);

  protected void evaluateTransitively(
      Task task, ImmutableList<ExprB> deps, Consumer<ValueB> result) {
    var depJs = map(deps, context::jobFor);
    var depResults = map(depJs, Job::evaluate);
    runWhenAllAvailable(depResults,
        () -> context.taskExecutor().enqueue(task, toInput(depResults), result));
  }

  private TupleB toInput(ImmutableList<Promise<ValueB>> depResults) {
    return context.bytecodeF().tuple(map(depResults, Promise::get));
  }
}
