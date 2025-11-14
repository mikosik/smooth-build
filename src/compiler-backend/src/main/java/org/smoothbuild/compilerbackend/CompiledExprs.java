package org.smoothbuild.compilerbackend;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.evaluate.base.BExprAttributes;

public record CompiledExprs(List<BExpr> bExprs, BExprAttributes bExprAttributes) {}
