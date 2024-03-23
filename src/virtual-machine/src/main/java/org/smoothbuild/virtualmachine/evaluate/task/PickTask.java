package org.smoothbuild.virtualmachine.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class PickTask extends Task {
  public PickTask(BPick pick, BTrace trace) {
    super(pick, trace);
  }

  @Override
  public Output run(BTuple input, Container container) throws BytecodeException {
    var components = input.elements();
    checkArgument(components.size() == 2);
    int index = index(components).toJavaBigInteger().intValue();
    var elements = array(components).elements(BValue.class);
    if (index < 0 || elements.size() <= index) {
      container
          .log()
          .error("Index (" + index + ") out of bounds. Array size = " + elements.size() + ".");
      return new Output(null, container.messages());
    } else {
      return new Output(elements.get(index), container.messages());
    }
  }

  private BArray array(List<BValue> components) {
    return (BArray) components.get(0);
  }

  private BInt index(List<BValue> components) {
    return (BInt) components.get(1);
  }
}
