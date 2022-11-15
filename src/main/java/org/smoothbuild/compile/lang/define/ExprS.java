package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends WithLoc
    permits CallS, InstS, MonoizeS, OrderS, ParamRefS, SelectS {
  public TypeS evalT();
}
