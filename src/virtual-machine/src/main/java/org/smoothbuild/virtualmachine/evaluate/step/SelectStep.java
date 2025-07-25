package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BSelect;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class SelectStep extends Step {
  public SelectStep(BSelect select, Trace trace) {
    super("select", select.hash(), select.evaluationType(), trace);
  }

  @Override
  public BOutput run(BTuple input, Container container) throws BytecodeException {
    var components = input.elements();
    checkArgument(components.size() == 2);
    var tuple = selectable(components);
    var index = index(components);
    return bOutput(tuple.get(index.toJavaBigInteger().intValue()), container.messages());
  }

  private BTuple selectable(List<BValue> components) {
    return (BTuple) components.get(0);
  }

  private BInt index(List<BValue> components) {
    return (BInt) components.get(1);
  }
}
