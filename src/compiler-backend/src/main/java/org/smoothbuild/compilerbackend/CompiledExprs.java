package org.smoothbuild.compilerbackend;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.report.BExprAttributes;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;

public record CompiledExprs(List<BExpr> bExprs, BExprAttributes bExprAttributes) {}
