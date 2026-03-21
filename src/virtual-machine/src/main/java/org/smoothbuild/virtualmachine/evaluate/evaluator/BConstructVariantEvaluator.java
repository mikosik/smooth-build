package org.smoothbuild.virtualmachine.evaluate.evaluator;

import static org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructVariant.CHOICE_INDEX;
import static org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructVariant.INDEX_INDEX;
import static org.smoothbuild.virtualmachine.evaluate.plugin.BOutput.bOutput;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.Trace;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BConstructVariant;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BInt;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.BOutput;
import org.smoothbuild.virtualmachine.evaluate.plugin.Container;

public final class BConstructVariantEvaluator extends BOperationEvaluator<BConstructVariant> {
  public BConstructVariantEvaluator(BConstructVariant constructVariant, Trace trace) {
    super(constructVariant, trace);
  }

  @Override
  public BOutput evaluate(BTuple evaluatedSubExprs, Container container) throws BytecodeException {
    var components = evaluatedSubExprs.elements();
    var index = index(components);
    var choice = choice(components);
    var variant = container.factory().variant(operation().evaluationType(), index, choice);
    return bOutput(variant, container.messages());
  }

  private static BInt index(List<BValue> components) {
    return (BInt) components.get(INDEX_INDEX);
  }

  private static BValue choice(List<BValue> components) {
    return components.get(CHOICE_INDEX);
  }
}
