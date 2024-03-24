package org.smoothbuild.compilerbackend;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.BExpr;

public record CompiledExprs(List<SExpr> sExprs, List<BExpr> bExprs, BsMapping bsMapping) {
  public static CompiledExprs compilationResult(
      List<SExpr> sExprs, List<BExpr> bExprs, BsMapping bsMapping) {
    return new CompiledExprs(sExprs, bExprs, bsMapping);
  }

  public CompiledExprs {
    checkArgument(sExprs.size() == bExprs.size());
  }
}
