package org.smoothbuild.vm.task;

import static org.smoothbuild.vm.execute.TaskKind.COMBINE;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TraceB;

public final class CombineTask extends Task {
  public CombineTask(CombineB combineB, TraceB trace) {
    super(combineB, COMBINE, trace);
  }

  @Override
  public Output run(TupleB input, Container container) {
    return new Output(input, container.messages());
  }
}
