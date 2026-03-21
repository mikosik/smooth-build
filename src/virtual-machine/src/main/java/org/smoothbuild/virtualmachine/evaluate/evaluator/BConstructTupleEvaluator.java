package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class BConstructTupleEvaluator extends BOperationEvaluator<BConstructTuple> {
  public BConstructTupleEvaluator(BConstructTuple constructTuple, Trace trace) {
    super(constructTuple, trace);
  }

  @Override
  public BOutput evaluate(BTuple evaluatedSubExprs, Container container) throws BytecodeException {
    return bOutput(evaluatedSubExprs, container.messages());
  }
}
