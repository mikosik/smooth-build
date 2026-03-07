package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet.ARRAY_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet.INDEX_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArrayGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class BArrayGetEvaluator extends BOperationEvaluator<BArrayGet> {
  public BArrayGetEvaluator(BArrayGet arrayGet, Trace trace) {
    super(arrayGet, trace);
  }

  @Override
  public BOutput evaluate(BTuple evaluatedSubExprs, Container container) throws BytecodeException {
    var elements = evaluatedSubExprs.elements();
    int index = index(elements).toJavaBigInteger().intValue();
    var array = array(elements).elements(BValue.class);
    if (index < 0 || array.size() <= index) {
      container
          .log()
          .error("Index (" + index + ") out of bounds. Array size = " + array.size() + ".");
      return bOutput(container.messages());
    } else {
      return bOutput(array.get(index), container.messages());
    }
  }

  private static BArray array(List<BValue> components) {
    return (BArray) components.get(ARRAY_INDEX);
  }

  private static BInt index(List<BValue> components) {
    return (BInt) components.get(INDEX_INDEX);
  }
}
