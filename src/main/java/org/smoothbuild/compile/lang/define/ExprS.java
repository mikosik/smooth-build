package org.smoothbuild.compile.lang.define;

import org.smoothbuild.compile.lang.base.Located;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression.
 */
public sealed interface ExprS extends Located
    permits CallS, ConstantS, MonoizeS, OrderS, ParamRefS, SelectS {
  public TypeS evalT();
}
