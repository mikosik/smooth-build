package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BPick.DATA_SEQ_SIZE;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BPick.INDEX_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BPick.PICKABLE_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.evaluator.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class PickEvaluator extends OperationEvaluator {
  public PickEvaluator(BPick pick, Trace trace) {
    super(pick, trace);
  }

  @Override
  public BOutput evaluate(BTuple subExprValues, Container container) throws BytecodeException {
    var elements = subExprValues.elements();
    checkArgument(elements.size() == DATA_SEQ_SIZE);
    int index = index(elements).toJavaBigInteger().intValue();
    var pickable = pickable(elements).elements(BValue.class);
    if (index < 0 || pickable.size() <= index) {
      container
          .log()
          .error("Index (" + index + ") out of bounds. Array size = " + pickable.size() + ".");
      return bOutput(container.messages());
    } else {
      return bOutput(pickable.get(index), container.messages());
    }
  }

  private static BArray pickable(List<BValue> components) {
    return (BArray) components.get(PICKABLE_INDEX);
  }

  private static BInt index(List<BValue> components) {
    return (BInt) components.get(INDEX_INDEX);
  }
}
