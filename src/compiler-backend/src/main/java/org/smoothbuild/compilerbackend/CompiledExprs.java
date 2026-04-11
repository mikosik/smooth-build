package org.smoothbuild.compilerbackend;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Map;
import org.smoothbuild.compilerfrontend.lang.define.SExpr;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BExpr;
import org.smoothbuild.virtualmachine.evaluate.base.DebugSymbols;

public record CompiledExprs(
    List<SExpr> sExprs, List<BExpr> bExprs, Map<Hash, DebugSymbols> debugSymbols) {}
