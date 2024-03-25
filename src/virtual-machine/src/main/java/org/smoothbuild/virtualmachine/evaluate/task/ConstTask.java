package org.smoothbuild.virtualmachine.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.FAST;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class ConstTask extends Task {
  public ConstTask(BValue value, BTrace trace) {
    super(value, trace, FAST);
  }

  public BValue valueB() {
    return (BValue) exprB();
  }

  @Override
  public Output run(BTuple input, Container container) throws BytecodeException {
    checkArgument(input.elements().isEmpty());
    return new Output(valueB(), container.messages());
  }
}
