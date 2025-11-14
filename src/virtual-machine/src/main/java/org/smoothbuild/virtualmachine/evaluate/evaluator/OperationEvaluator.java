package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.base.Purity.PURE;

import java.io.IOException;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOperation;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.base.Purity;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

/**
 * Evaluates single {@link org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr BExpr}.
 * Evaluation of sub-expressions are done separately and are passed to this evaluator's
 * {@linkplain #evaluate(BTuple, Container)} method.
 *
 * This class is thread-safe.
 */
public abstract sealed class OperationEvaluator
    permits BChooseEvaluator,
    BCombineEvaluator,
    BInvokeEvaluator,
    BOrderEvaluator,
    BPickEvaluator,
    BSelectEvaluator {
  private final BOperation operation;
  private final Trace trace;

  public OperationEvaluator(BOperation operation, Trace trace) {
    this.operation = operation;
    this.trace = trace;
  }

  public BOperation operation() {
    return operation;
  }

  public Trace trace() {
    return trace;
  }

  public Purity purity(BTuple subExprValues) throws BytecodeException {
    return PURE;
  }

  public abstract BOutput evaluate(BTuple subExprValues, Container container) throws IOException;
}
