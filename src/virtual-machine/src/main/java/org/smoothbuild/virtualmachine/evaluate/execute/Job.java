package org.smoothbuild.virtualmachine.evaluate.execute;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.util.function.Function;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class Job {
  private final BExpr expr;
  private final List<Job> environment;
  private final BTrace trace;
  private final Supplier<Promise<Maybe<BValue>>> evaluator;

  public Job(
      BExpr expr,
      List<Job> environment,
      BTrace trace,
      Function<Job, Promise<Maybe<BValue>>> evaluator) {
    this.expr = expr;
    this.environment = environment;
    this.trace = trace;
    this.evaluator = Suppliers.memoize(() -> evaluator.apply(this));
  }

  public BExpr expr() {
    return expr;
  }

  public List<Job> environment() {
    return environment;
  }

  public BTrace trace() {
    return trace;
  }

  public Promise<Maybe<BValue>> evaluate() {
    return evaluator.get();
  }
}
