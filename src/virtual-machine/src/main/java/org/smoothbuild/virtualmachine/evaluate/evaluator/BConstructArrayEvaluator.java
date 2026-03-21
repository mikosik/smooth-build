package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class BConstructArrayEvaluator extends BOperationEvaluator<BConstructArray> {
  public BConstructArrayEvaluator(BConstructArray constructArray, Trace trace) {
    super(constructArray, trace);
  }

  @Override
  public BOutput evaluate(BTuple evaluatedSubExprs, Container container) throws BytecodeException {
    BArray array = container
        .factory()
        .arrayBuilder(operation().evaluationType())
        .addAll(evaluatedSubExprs.elements())
        .build();
    return bOutput(array, container.messages());
  }
}
