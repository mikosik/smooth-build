package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static com.google.common.base.Preconditions.checkArgument;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose.CHOSEN_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose.DATA_SEQ_SIZE;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose.INDEX_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BChoose;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BChoiceType;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class ChooseEvaluator extends OperationEvaluator {
  public ChooseEvaluator(BChoose choose, Trace trace) {
    super(choose, trace);
  }

  @Override
  public BOutput evaluate(BTuple subExprValues, Container container) throws BytecodeException {
    var components = subExprValues.elements();
    checkArgument(components.size() == DATA_SEQ_SIZE);
    var index = index(components);
    var chosen = chosen(components);
    var choice =
        container.factory().choice((BChoiceType) operation().evaluationType(), index, chosen);
    return bOutput(choice, container.messages());
  }

  private static BInt index(List<BValue> components) {
    return (BInt) components.get(INDEX_INDEX);
  }

  private static BValue chosen(List<BValue> components) {
    return components.get(CHOSEN_INDEX);
  }
}
