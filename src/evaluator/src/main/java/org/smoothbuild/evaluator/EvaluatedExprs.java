package org.smoothbuild.evaluator;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;

public record EvaluatedExprs(List<ExprS> exprSs, List<ValueB> valuesB) {
  public static EvaluatedExprs evaluatedExprs(List<ExprS> exprSs, List<ValueB> valuesB) {
    return new EvaluatedExprs(exprSs, valuesB);
  }

  public EvaluatedExprs {
    checkArgument(exprSs.size() == valuesB.size());
  }
}
