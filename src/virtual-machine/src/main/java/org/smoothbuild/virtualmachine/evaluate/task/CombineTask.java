package org.smoothbuild.virtualmachine.evaluate.task;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.CombineB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;

public final class CombineTask extends Task {
  public CombineTask(CombineB combineB, TraceB trace) {
    super(combineB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) throws BytecodeException {
    return new Output(input, container.messages());
  }
}
