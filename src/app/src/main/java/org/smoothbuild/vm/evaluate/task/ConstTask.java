package org.smoothbuild.vm.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.evaluate.task.Purity.FAST;

import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.execute.TraceB;

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
