package org.smoothbuild.evaluator;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;

public record EvaluatedExprs(List<SExpr> sExprs, List<BValue> bValues) {
  public static EvaluatedExprs evaluatedExprs(List<SExpr> sExprs, List<BValue> bValues) {
    return new EvaluatedExprs(sExprs, bValues);
  }

  public EvaluatedExprs {
    checkArgument(sExprs.size() == bValues.size());
  }
}
