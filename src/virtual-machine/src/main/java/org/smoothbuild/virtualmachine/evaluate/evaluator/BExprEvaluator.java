package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.evaluator.Purity.PURE;

import java.io.IOException;
import java.util.Objects;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
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
  private final String name;
  private final Hash hash;
  private final BType evaluationType;
  private final Trace trace;

  public BExprEvaluator(String name, Hash hash, BType evaluationType, Trace trace) {
    this.name = name;
    this.hash = hash;
    this.evaluationType = evaluationType;
    this.trace = trace;
  }

  public String name() {
    return name;
  }

  public Trace trace() {
    return trace;
  }

  public BType evaluationType() {
    return evaluationType;
  }

  public Purity purity(BTuple input) throws BytecodeException {
    return PURE;
  }

  public abstract BOutput evaluate(BTuple input, Container container) throws IOException;

  @Override
  public int hashCode() {
    return hash.hashCode();
  }

  @Override
  public boolean equals(Object object) {
    return object instanceof BExprEvaluator that
        && Objects.equals(this.getClass(), that.getClass())
        && Objects.equals(this.hash, that.hash)
        && Objects.equals(this.trace, that.trace);
  }
}
