package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.SELECT;

import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public final class SelectTask extends ExecutableTask {
  public SelectTask(TypeB outputT, TagLoc tagLoc, TraceS trace) {
    super(outputT, SELECT, tagLoc, trace);
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    var components = input.items();
    checkArgument(components.size() == 2);
    var tuple = selectable(components);
    var index = index(components);
    return new Output(tuple.get(index.toJ().intValue()), nativeApi.messages());
  }

  private TupleB selectable(ImmutableList<InstB> components) {
    return (TupleB) components.get(0);
  }

  private IntB index(ImmutableList<InstB> components) {
    return (IntB) components.get(1);
  }
}
