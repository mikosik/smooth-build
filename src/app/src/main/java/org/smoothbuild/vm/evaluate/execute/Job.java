package org.smoothbuild.vm.evaluate.execute;

import static java.util.Objects.requireNonNull;

import java.util.concurrent.atomic.AtomicReference;

import org.smoothbuild.util.concurrent.PromisedValue;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;

public final class Job {
  private final ExprB exprB;
  private final JobContext context;
  private final AtomicReference<PromisedValue<ValueB>> promiseReference;

  public Job(ExprB exprB, JobContext jobContext) {
    this.exprB = exprB;
    this.context = jobContext;
    this.promiseReference = new AtomicReference<>(null);
  }

  public boolean initializePromise(PromisedValue<ValueB> newPromise) {
    return promiseReference.compareAndSet(null, requireNonNull(newPromise));
  }

  public PromisedValue<ValueB> promise() {
    return promiseReference.get();
  }

  public ExprB exprB() {
    return exprB;
  }

  public JobContext context() {
    return context;
  }
}
