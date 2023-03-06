package org.smoothbuild.vm.evaluate.task;

import org.smoothbuild.vm.bytecode.expr.oper.CombineB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.execute.TraceB;

public final class CombineTask extends Task {
  public CombineTask(CombineB combineB, TraceB trace) {
    super(combineB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) {
    return new Output(input, container.messages());
  }
}
