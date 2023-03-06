package org.smoothbuild.compile.fs.lang.define;

import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends Located
    permits CallS, CombineS, ConstantS, InstantiateS, OrderS, SelectS {
  public TypeS evaluationT();
}
