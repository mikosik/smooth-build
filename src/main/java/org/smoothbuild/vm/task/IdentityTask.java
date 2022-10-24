package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.task.Purity.FAST;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TaskKind;

public final class IdentityTask extends Task {
  public IdentityTask(TypeB type, TaskKind kind, TagLoc tagLoc, TraceS trace) {
    super(type, kind, tagLoc, trace, FAST);
  }

  @Override
  public Output run(TupleB input, Container container) {
    var items = input.items();
    checkArgument(items.size() == 1);
    return new Output(items.get(0), container.messages());
  }
}
