package org.smoothbuild.compile.lang.define;

import java.util.function.Function;

import org.smoothbuild.compile.lang.base.ExprInfo;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.compile.lang.type.VarS;

/**
 * Expression.
 */
public sealed interface ExprS extends ExprInfo permits ValS, OperS {
  public TypeS type();
  public ExprS mapVars(Function<VarS, TypeS> mapper);
}
