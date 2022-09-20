package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.SELECT;
import static org.smoothbuild.vm.task.TaskHashes.selectTaskHash;

import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.expr.val.ValB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class SelectTask extends Task {
  public SelectTask(TypeB outputT, LabeledLoc labeledLoc) {
    super(outputT, SELECT, labeledLoc);
  }

  @Override
  public Hash hash() {
    return selectTaskHash();
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    var components = input.items();
    checkArgument(components.size() == 2);
    var tuple = selectable(components);
    var index = index(components);
    return new Output(tuple.get(index.toJ().intValue()), nativeApi.messages());
  }

  private TupleB selectable(ImmutableList<ValB> components) {
    return (TupleB) components.get(0);
  }

  private IntB index(ImmutableList<ValB> components) {
    return (IntB) components.get(1);
  }
}
