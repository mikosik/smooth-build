package org.smoothbuild.virtualmachine.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.PickB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.IntB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.TraceB;

public final class PickTask extends Task {
  public PickTask(PickB pickB, TraceB trace) {
    super(pickB, trace);
  }

  @Override
  public Output run(TupleB input, Container container) throws BytecodeException {
    var components = input.elements();
    checkArgument(components.size() == 2);
    int index = index(components).toJ().intValue();
    var elems = array(components).elements(ValueB.class);
    if (index < 0 || elems.size() <= index) {
      container
          .log()
          .error("Index (" + index + ") out of bounds. Array size = " + elems.size() + ".");
      return new Output(null, container.messages());
    } else {
      return new Output(elems.get(index), container.messages());
    }
  }

  private ArrayB array(List<ValueB> components) {
    return (ArrayB) components.get(0);
  }

  private IntB index(List<ValueB> components) {
    return (IntB) components.get(1);
  }
}
