package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.PICK;
import static org.smoothbuild.vm.task.TaskHashes.pickTaskHash;

import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.IntB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.plugin.NativeApi;

import com.google.common.collect.ImmutableList;

public class PickTask extends Task {
  public PickTask(TypeB outputT, LabeledLoc labeledLoc) {
    super(outputT, PICK, labeledLoc);
  }

  @Override
  public Hash hash() {
    return pickTaskHash();
  }

  @Override
  public Output run(TupleB input, NativeApi nativeApi) {
    var components = input.items();
    checkArgument(components.size() == 2);
    int index = index(components).toJ().intValue();
    var elems = array(components).elems(InstB.class);
    if (index < 0 || elems.size() <= index) {
      nativeApi.log().error(
          "Index (" + index + ") out of bounds. Array size = " + elems.size() + ".");
      return new Output(null, nativeApi.messages());
    } else {
      return new Output(elems.get(index), nativeApi.messages());
    }
  }

  private ArrayB array(ImmutableList<InstB> components) {
    return (ArrayB) components.get(0);
  }

  private IntB index(ImmutableList<InstB> components) {
    return (IntB) components.get(1);
  }
}
