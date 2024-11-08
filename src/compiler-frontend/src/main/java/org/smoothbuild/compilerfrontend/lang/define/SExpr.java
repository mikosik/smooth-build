package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression.
 */
public sealed interface SExpr extends Located
    permits SCall, SCombine, SConstant, SInstantiate, SOrder, SSelect {
  public SType evaluationType();
}
