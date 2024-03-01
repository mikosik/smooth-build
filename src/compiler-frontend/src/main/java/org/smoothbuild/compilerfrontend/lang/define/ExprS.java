package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Located;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends Located
    permits CallS, CombineS, ConstantS, InstantiateS, OrderS, SelectS {
  public TypeS evaluationT();
}
