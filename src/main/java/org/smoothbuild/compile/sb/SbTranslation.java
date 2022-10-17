package org.smoothbuild.compile.sb;

import org.smoothbuild.bytecode.expr.ExprB;

import com.google.common.collect.ImmutableList;

public record SbTranslation(ImmutableList<ExprB> exprBs, BsMapping bsMapping) {
}
