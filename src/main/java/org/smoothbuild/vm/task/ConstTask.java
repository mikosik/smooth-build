package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.CONST;
import static org.smoothbuild.vm.task.Purity.FAST;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TraceB;

public final class ConstTask extends Task {
  public ConstTask(InstB instB, TraceB trace) {
    super(instB, CONST, trace, FAST);
  }

  public InstB instB() {
    return (InstB) exprB();
  }

  @Override
  public Output run(TupleB input, Container container) {
    checkArgument(input.items().size() == 0);
    return new Output(instB(), container.messages());
  }
}
