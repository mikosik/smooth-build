package org.smoothbuild.vm.job;

import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.concurrent.Promises.runWhenAllAvailable;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.util.concurrent.Promise;
import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.task.Task;

import com.google.common.collect.ImmutableList;

public abstract class Job {
  private volatile Promise<InstB> promise;
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

  public final Promise<InstB> evaluate() {
    // Double-checked locking.
    Promise<InstB> result = promise;
    if (result != null) {
      return result;
    }
    synchronized (this) {
      result = promise;
      if (result == null) {
        promise = result = evaluateImpl();
      }
      return result;
    }
  }

  protected abstract Promise<InstB> evaluateImpl();

  protected PromisedValue<InstB> evaluateTransitively(Task task, ImmutableList<ExprB> deps) {
    var result = new PromisedValue<InstB>();
    var depJs = map(deps, context::jobFor);
    var depResults = map(depJs, Job::evaluate);
    runWhenAllAvailable(depResults,
        () -> context.taskExecutor().enqueue(task, toInput(depResults), result));
    return result;
  }

  private TupleB toInput(ImmutableList<Promise<InstB>> depResults) {
    return context.bytecodeF().tuple(map(depResults, Promise::get));
  }
}
