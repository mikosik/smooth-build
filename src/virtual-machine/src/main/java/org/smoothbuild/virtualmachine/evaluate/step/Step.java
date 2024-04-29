package org.smoothbuild.virtualmachine.evaluate.step;

import static org.smoothbuild.virtualmachine.evaluate.step.Purity.PURE;

import java.util.Objects;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

/**
 * Evaluation of single Bytecode expression (BExpr).
 * Evaluation of sub-expressions are separate steps.
 * This class is thread-safe.
 */
public abstract sealed class Step
    permits CombineStep, ConstStep, InvokeStep, OrderStep, PickStep, SelectStep {
  private final Hash hash;
  private final BType evaluationType;
  private final BTrace trace;

  public Step(BExpr expr, BTrace trace) {
    this.hash = expr.hash();
    this.evaluationType = expr.evaluationType();
    this.trace = trace;
  }

  public BTrace trace() {
    return trace;
  }

  public BType evaluationType() {
    return evaluationType;
  }

  public Purity purity(BTuple input) throws BytecodeException {
    return PURE;
  }

  public abstract BOutput run(BTuple input, Container container) throws BytecodeException;

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof Step that
        && Objects.equals(this.getClass(), that.getClass())
        && Objects.equals(this.hash, that.hash)
        && Objects.equals(this.trace, that.trace);
  }
}
