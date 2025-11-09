package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.evaluator.BOutput.bOutput;

import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class BOrderEvaluator extends BExprEvaluator {
  public BOrderEvaluator(BOrder order, Trace trace) {
    super("order", order.hash(), order.evaluationType(), trace);
  }

  @Override
  public BOutput evaluate(BTuple input, Container container) throws BytecodeException {
    BArray array = container
        .factory()
        .arrayBuilder((BArrayType) evaluationType())
        .addAll(input.elements())
        .build();
    return bOutput(array, container.messages());
  }
}
