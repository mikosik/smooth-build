package org.smoothbuild.compilerbackend;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.BsMapping;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;

public record CompiledExprs(List<SExpr> sExprs, List<BExpr> bExprs, BsMapping bsMapping) {
  public static CompiledExprs compilationResult(
      List<SExpr> sExprs, List<BExpr> bExprs, BsMapping bsMapping) {
    return new CompiledExprs(sExprs, bExprs, bsMapping);
  }

  public CompiledExprs {
    checkArgument(sExprs.size() == bExprs.size());
  }
}
