package org.smoothbuild.virtualmachine.evaluate.execute;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;

public final class Job {
  private final BExpr expr;
  private final List<Job> environment;
  private final BTrace trace;

  public Job(BExpr expr, List<Job> environment, BTrace trace) {
    this.expr = expr;
    this.environment = environment;
    this.trace = trace;
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
}
