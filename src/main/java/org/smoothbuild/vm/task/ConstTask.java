package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.task.Purity.FAST;

import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TraceB;

public final class ConstTask extends Task {
  public ConstTask(ValueB valueB, TraceB trace) {
    super(valueB, trace, FAST);
  }

  public ValueB valueB() {
    return (ValueB) exprB();
  }

  @Override
  public Output run(TupleB input, Container container) {
    checkArgument(input.items().size() == 0);
    return new Output(valueB(), container.messages());
  }
}
