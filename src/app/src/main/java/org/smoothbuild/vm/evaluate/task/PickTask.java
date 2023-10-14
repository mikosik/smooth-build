package org.smoothbuild.vm.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.vm.bytecode.expr.oper.PickB;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.IntB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.evaluate.compute.Container;
import org.smoothbuild.vm.evaluate.execute.TraceB;

import io.vavr.collection.Array;

public final class PickTask extends Task {
  public PickTask(PickB pickB, TraceB trace) {
    super(pickB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) {
    var components = input.elements();
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

  private ArrayB array(Array<ValueB> components) {
    return (ArrayB) components.get(0);
  }

  private IntB index(Array<ValueB> components) {
    return (IntB) components.get(1);
  }
}
