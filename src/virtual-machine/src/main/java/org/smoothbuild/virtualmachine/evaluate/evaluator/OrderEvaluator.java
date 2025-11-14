package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BOrder;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BArrayType;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class OrderEvaluator extends OperationEvaluator {
  public OrderEvaluator(BOrder order, Trace trace) {
    super(order, trace);
  }

  @Override
  public BOutput evaluate(BTuple subExprValues, Container container) throws BytecodeException {
    BArray array = container
        .factory()
        .arrayBuilder((BArrayType) operation().evaluationType())
        .addAll(subExprValues.elements())
        .build();
    return bOutput(array, container.messages());
  }
}
