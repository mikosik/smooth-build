package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.LabeledLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends LabeledLoc permits InstS, OperS {
  public TypeS evalT();
}
