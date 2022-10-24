package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.vm.execute.TaskKind.PICK;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.InstB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.type.inst.TypeB;
import org.smoothbuild.compile.lang.base.TagLoc;
import org.smoothbuild.compile.lang.define.TraceS;
import org.smoothbuild.vm.compute.Container;

import com.google.common.collect.ImmutableList;

public final class PickTask extends Task {
  public PickTask(TypeB outputT, TagLoc tagLoc, TraceS trace) {
    super(outputT, PICK, tagLoc, trace);
  }

  @Override
  public Output run(TupleB input, Container container) {
    var components = input.items();
    checkArgument(components.size() == 2);
    int index = index(components).toJ().intValue();
    var elems = array(components).elems(InstB.class);
    if (index < 0 || elems.size() <= index) {
      container.log().error(
          "Index (" + index + ") out of bounds. Array size = " + elems.size() + ".");
      return new Output(null, container.messages());
    } else {
      return new Output(elems.get(index), container.messages());
    }
  }

  private ArrayB array(ImmutableList<InstB> components) {
    return (ArrayB) components.get(0);
  }

  private IntB index(ImmutableList<InstB> components) {
    return (IntB) components.get(1);
  }
}
