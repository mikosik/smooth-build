package org.smoothbuild.virtualmachine.evaluate.step;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.evaluate.compute.Container;

public final class ChooseStep extends Step {
  public ChooseStep(BChoose choose, Trace trace) {
    super("choose", choose.hash(), choose.evaluationType(), trace);
  }

  @Override
  public BOutput run(BTuple input, Container container) throws BytecodeException {
    var components = input.elements();
    checkArgument(components.size() == 2);
    var index = index(components);
    var choice = chosen(components);
    var choose = container.factory().choice((BChoiceType) evaluationType(), index, choice);
    return bOutput(choose, container.messages());
  }

  private BInt index(List<BValue> components) {
    return (BInt) components.get(0);
  }

  private BValue chosen(List<BValue> components) {
    return components.get(1);
  }
}
