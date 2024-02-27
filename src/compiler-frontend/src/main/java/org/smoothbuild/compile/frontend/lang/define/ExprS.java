package org.smoothbuild.compile.frontend.lang.define;

import org.smoothbuild.compile.frontend.lang.base.location.Located;
import org.smoothbuild.compile.frontend.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends Located
    permits CallS, CombineS, ConstantS, InstantiateS, OrderS, SelectS {
  public TypeS evaluationT();
}
