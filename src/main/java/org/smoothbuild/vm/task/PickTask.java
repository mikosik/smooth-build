package org.smoothbuild.vm.task;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.bytecode.expr.inst.ArrayB;
import org.smoothbuild.bytecode.expr.inst.IntB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.bytecode.expr.inst.ValueB;
import org.smoothbuild.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.compute.Container;
import org.smoothbuild.vm.execute.TraceB;

import com.google.common.collect.ImmutableList;

public final class PickTask extends Task {
  public PickTask(PickB pickB, TraceB trace) {
    super(pickB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) {
    var components = input.items();
    checkArgument(components.size() == 2);
    int index = index(components).toJ().intValue();
    var elems = array(components).elems(ValueB.class);
    if (index < 0 || elems.size() <= index) {
      container.log().error(
          "Index (" + index + ") out of bounds. Array size = " + elems.size() + ".");
      return new Output(null, container.messages());
    } else {
      return new Output(elems.get(index), container.messages());
    }
  }

  private ArrayB array(ImmutableList<ValueB> components) {
    return (ArrayB) components.get(0);
  }

  private IntB index(ImmutableList<ValueB> components) {
    return (IntB) components.get(1);
  }
}
