package org.smoothbuild.compilerbackend;

import static com.google.common.base.Preconditions.checkArgument;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.lang.define.ExprS;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;

public record CompiledExprs(List<ExprS> exprSs, List<ExprB> exprBs, BsMapping bsMapping) {
  public static CompiledExprs compilationResult(
      List<ExprS> exprSs, List<ExprB> exprBs, BsMapping bsMapping) {
    return new CompiledExprs(exprSs, exprBs, bsMapping);
  }

  public CompiledExprs {
    checkArgument(exprSs.size() == exprBs.size());
  }
}
