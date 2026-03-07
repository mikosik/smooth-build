package org.smoothbuild.compilerfrontend.lang.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression.
 */
public sealed interface SExpr extends HasLocation
    permits SCall,
        SCreateTuple,
        SConstant,
        SInstantiate,
        SLambda,
        SMonoReference,
        SCreateArray,
        SStructGet,
        STupleGet {
  public String toSourceCode();

  public SType evaluationType();
}
