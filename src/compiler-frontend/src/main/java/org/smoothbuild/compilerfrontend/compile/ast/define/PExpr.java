package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression in smooth language.
 */
public abstract sealed class PExpr implements HasLocation
    permits PCall,
        PCombine,
        PInstantiate,
        PLambda,
        PLiteral,
        PNamedArg,
        POrder,
        PStructSelect,
        PTupleSelect {
  private final Location location;
  private SType type;

  public PExpr(Location location) {
    this.location = location;
  }

  public SType sType() {
    return type;
  }

  public void setSType(SType type) {
    this.type = type;
  }

  @Override
  public Location location() {
    return location;
  }
}
