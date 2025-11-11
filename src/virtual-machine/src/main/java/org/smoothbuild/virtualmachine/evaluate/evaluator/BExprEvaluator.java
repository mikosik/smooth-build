package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.evaluator.Purity.PURE;

import java.io.IOException;
import java.util.Objects;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

/**
 * Evaluates single {@link org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr BExpr}.
 * Evaluation of sub-expressions are done separately and are passed to this evaluator's
 * {@linkplain #evaluate(BTuple, Container)} method.
 *
 * This class is thread-safe.
 */
public abstract sealed class BExprEvaluator
    permits BChooseEvaluator,
        BCombineEvaluator,
        BInvokeEvaluator,
        BOrderEvaluator,
        BPickEvaluator,
        BSelectEvaluator {
  private final BOperation operation;
  private final Trace trace;

  public BExprEvaluator(BOperation operation, Trace trace) {
    this.operation = operation;
    this.trace = trace;
  }

  public BOperation operation() {
    return operation;
  }

  public String name() {
    return operation().name();
  }

  public BType evaluationType() {
    return operation().evaluationType();
  }

  public Trace trace() {
    return trace;
  }

  public Purity purity(BTuple subExprValues) throws BytecodeException {
    return PURE;
  }

  public abstract BOutput evaluate(BTuple subExprValues, Container container) throws IOException;

  @Override
  public int hashCode() {
    return Objects.hash(this.getClass(), this.operation, this.trace);
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof BExprEvaluator that
        && Objects.equals(this.getClass(), that.getClass())
        && Objects.equals(this.operation, that.operation)
        && Objects.equals(this.trace, that.trace);
  }
}
