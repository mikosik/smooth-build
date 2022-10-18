package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.task.TaskHashes.identityTaskHash;

import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;
import org.smoothbuild.vm.execute.TaskKind;

public class IdentityTask extends ExecutableTask {
  public IdentityTask(TypeB type, TaskKind kind, TagLoc tagLoc, TraceS trace) {
    super(type, kind, tagLoc, trace, identityTaskHash());
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    var items = input.items();
    checkArgument(items.size() == 1);
    return new Output(items.get(0), nativeApi.messages());
  }
}
