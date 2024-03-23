package org.smoothbuild.evaluator;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;

public record EvaluatedExprs(List<ExprS> exprSs, List<BValue> bValues) {
  public static EvaluatedExprs evaluatedExprs(List<ExprS> exprSs, List<BValue> bValues) {
    return new EvaluatedExprs(exprSs, bValues);
  }

  public EvaluatedExprs {
    checkArgument(exprSs.size() == bValues.size());
  }
}
