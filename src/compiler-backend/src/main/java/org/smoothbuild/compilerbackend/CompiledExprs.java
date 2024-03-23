package org.smoothbuild.compilerbackend;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;

public record CompiledExprs(List<ExprS> exprSs, List<BExpr> bExprs, BsMapping bsMapping) {
  public static CompiledExprs compilationResult(
      List<ExprS> exprSs, List<BExpr> bExprs, BsMapping bsMapping) {
    return new CompiledExprs(exprSs, bExprs, bsMapping);
  }

  public CompiledExprs {
    checkArgument(exprSs.size() == bExprs.size());
  }
}
