package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.function.Function1.memoizer;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.concurrent.Promise;
import org.smoothbuild.common.function.Function1;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public final class Job {
  private final BExpr expr;
  private final List<Job> environment;
  private final BTrace trace;
  private final Function1<BEvaluate, Promise<Maybe<BValue>>, BytecodeException> evaluation;

  public Job(BExpr expr, List<Job> environment, BTrace trace) {
    this.expr = expr;
    this.environment = environment;
    this.trace = trace;
    this.evaluation = memoizer((BEvaluate b) -> b.doScheduleJob(this));
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

  /**
   * Schedules evaluation of this Job or returns cached evaluation if already scheduled.
   * Thanks to caching we avoid situation when multiple references to same lambda argument causes
   * scheduling references expression multiple times (Computation cache will not allow additional
   * computations but logs would be reported for cached computations).
   * Note that it won't work for lambda that is enclosed in outer lambda, and it references
   * parameter of outer lambda. Such inner lambda will undergo inlining before being used
   * (returned or called) so its BReferences to outer lambda parameters will be replaced
   * with actual BExpr and link to Job object will be lost.
   */
  public Promise<Maybe<BValue>> scheduleEvaluation(BEvaluate bEvaluate) throws BytecodeException {
    return evaluation.apply(bEvaluate);
  }
}
