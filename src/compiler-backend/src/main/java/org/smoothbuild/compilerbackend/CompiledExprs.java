package org.smoothbuild.compilerbackend;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprB;

public record CompiledExprs(List<ExprB> expressions, BsMapping bsMapping) {
  public static CompiledExprs compilationResult(List<ExprB> expressions, BsMapping bsMapping) {
    return new CompiledExprs(expressions, bsMapping);
  }
}
