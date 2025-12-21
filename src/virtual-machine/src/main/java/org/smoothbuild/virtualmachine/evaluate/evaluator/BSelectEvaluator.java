package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect.INDEX_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect.SELECTABLE_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class BSelectEvaluator extends OperationEvaluator<BSelect> {
  public BSelectEvaluator(BSelect select, Trace trace) {
    super(select, trace);
  }

  @Override
  public BOutput evaluate(BTuple evaluatedSubExprs, Container container) throws BytecodeException {
    var components = evaluatedSubExprs.elements();
    var selectable = selectable(components);
    var index = index(components);
    return bOutput(selectable.get(index.toJavaBigInteger().intValue()), container.messages());
  }

  private static BTuple selectable(List<BValue> components) {
    return (BTuple) components.get(SELECTABLE_INDEX);
  }

  private static BInt index(List<BValue> components) {
    return (BInt) components.get(INDEX_INDEX);
  }
}
