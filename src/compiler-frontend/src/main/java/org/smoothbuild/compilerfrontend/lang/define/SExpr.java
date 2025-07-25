package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression.
 */
public sealed interface SExpr extends HasLocation
    permits SCall,
        SCombine,
        SConstant,
        SInstantiate,
        SLambda,
        SMonoReference,
        SOrder,
        SStructSelect,
        STupleSelect {
  public String toSourceCode();

  public SType evaluationType();
}
