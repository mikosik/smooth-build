package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.evaluator.BOutput.bOutput;

import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BCombine;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class BCombineEvaluator extends BOperationEvaluator {
  public BCombineEvaluator(BCombine combine, Trace trace) {
    super(combine, trace);
  }

  @Override
  public BOutput evaluate(BTuple subExprValues, Container container) throws BytecodeException {
    return bOutput(subExprValues, container.messages());
  }
}
