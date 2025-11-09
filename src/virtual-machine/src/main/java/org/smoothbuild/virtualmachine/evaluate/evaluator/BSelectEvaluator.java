package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.evaluate.evaluator.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class BSelectEvaluator extends BExprEvaluator {
  public BSelectEvaluator(BSelect select, Trace trace) {
    super("select", select.hash(), select.evaluationType(), trace);
  }

  @Override
  public BOutput evaluate(BTuple subExprValues, Container container) throws BytecodeException {
    var components = subExprValues.elements();
    checkArgument(components.size() == 2);
    var tuple = selectable(components);
    var index = index(components);
    return bOutput(tuple.get(index.toJavaBigInteger().intValue()), container.messages());
  }

  private static BTuple selectable(List<BValue> components) {
    return (BTuple) components.get(0);
  }

  private static BInt index(List<BValue> components) {
    return (BInt) components.get(1);
  }
}
