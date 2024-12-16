package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression in smooth language.
 */
public abstract sealed class PExpr implements HasLocation
    permits PCall, PInstantiate, PLiteral, PNamedArg, POrder, PSelect {
  private SType type;
  private final Location location;

  public PExpr(Location location) {
    this.location = location;
  }

  @Override
  public Location location() {
    return location;
  }

  public SType sType() {
    return type;
  }

  public void setSType(SType type) {
    this.type = type;
  }
}
