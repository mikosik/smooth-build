package org.smoothbuild.virtualmachine.evaluate.task;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.oper.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;
import org.smoothbuild.virtualmachine.evaluate.execute.BTrace;

public final class SelectTask extends Task {
  public SelectTask(BSelect select, BTrace trace) {
    super(select, trace);
  }

  @Override
  public Output run(BTuple input, Container container) throws BytecodeException {
    var components = input.elements();
    checkArgument(components.size() == 2);
    var tuple = selectable(components);
    var index = index(components);
    return new Output(tuple.get(index.toJavaBigInteger().intValue()), container.messages());
  }

  private BTuple selectable(List<BValue> components) {
    return (BTuple) components.get(0);
  }

  private BInt index(List<BValue> components) {
    return (BInt) components.get(1);
  }
}
