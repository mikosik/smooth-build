package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet.INDEX_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet.TUPLE_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTupleGet;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class BTupleGetEvaluator extends BOperationEvaluator<BTupleGet> {
  public BTupleGetEvaluator(BTupleGet tupleGet, Trace trace) {
    super(tupleGet, trace);
  }

  @Override
  public BOutput evaluate(BTuple evaluatedSubExprs, Container container) throws BytecodeException {
    var subExprs = evaluatedSubExprs.elements();
    var tuple = tuple(subExprs);
    var index = index(subExprs);
    return bOutput(tuple.get(index.toJavaBigInteger().intValue()), container.messages());
  }

  private static BTuple tuple(List<BValue> components) {
    return (BTuple) components.get(TUPLE_INDEX);
  }

  private static BInt index(List<BValue> components) {
    return (BInt) components.get(INDEX_INDEX);
  }
}
