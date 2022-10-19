package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.CONST;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;

public final class ConstTask extends ExecutableTask {
  private final InstB instB;

  public ConstTask(InstB instB, TagLoc tagLoc, TraceS trace) {
    super(instB.type(), CONST, tagLoc, trace);
    this.instB = instB;
  }

  public InstB instB() {
    return instB;
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    checkArgument(input.items().size() == 0);
    return new Output(instB, nativeApi.messages());
  }
}
