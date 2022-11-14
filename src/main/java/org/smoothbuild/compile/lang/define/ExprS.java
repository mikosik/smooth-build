package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends WithLoc permits InstS, MonoizeS, OperS {
  public TypeS evalT();
}
