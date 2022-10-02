package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends LabeledLoc permits ValS, OperS {
  public TypeS type();
}
