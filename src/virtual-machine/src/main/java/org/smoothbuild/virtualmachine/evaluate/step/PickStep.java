package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BPick;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class PickStep extends Step {
  public PickStep(BPick pick, Trace trace) {
    super("pick", pick.hash(), pick.evaluationType(), trace);
  }

  @Override
  public BOutput run(BTuple input, Container container) throws BytecodeException {
    var components = input.elements();
    checkArgument(components.size() == 2);
    int index = index(components).toJavaBigInteger().intValue();
    var elements = array(components).elements(BValue.class);
    if (index < 0 || elements.size() <= index) {
      container
          .log()
          .error("Index (" + index + ") out of bounds. Array size = " + elements.size() + ".");
      return bOutput(null, container.messages());
    } else {
      return bOutput(elements.get(index), container.messages());
    }
  }

  private BArray array(List<BValue> components) {
    return (BArray) components.get(0);
  }

  private BInt index(List<BValue> components) {
    return (BInt) components.get(1);
  }
}
