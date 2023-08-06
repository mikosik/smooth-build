package org.smoothbuild.virtualmachine.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.evaluate.task.Purity.FAST;

import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;

public final class ConstTask extends Task {
  public ConstTask(ValueB valueB, TraceB trace) {
    super(valueB, trace, FAST);
  }

  public ValueB valueB() {
    return (ValueB) exprB();
  }

  @Override
  public Output run(TupleB input, Container container) throws BytecodeException {
    checkArgument(input.elements().isEmpty());
    return new Output(valueB(), container.messages());
  }
}
