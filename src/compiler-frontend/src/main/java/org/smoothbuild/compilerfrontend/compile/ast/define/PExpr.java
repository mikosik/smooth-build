package org.smoothbuild.compilerfrontend.compile.ast.define;

import static org.smoothbuild.common.base.Check.checkInitializedToNotNull;

import org.jspecify.annotations.Nullable;
import org.smoothbuild.common.log.location.HasLocation;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression in smooth language.
 */
public abstract sealed class PExpr implements HasLocation
    permits PCall,
        PCreateTuple,
        PInstantiate,
        PLambda,
        PLiteral,
        PNamedArg,
        PCreateArray,
        PStructGet,
        PTupleGet {
  private final Location location;
  private @Nullable SType type;

  public PExpr(Location location) {
    this.location = location;
  }

  public SType sType() {
    return checkInitializedToNotNull(type, "type");
  }

  public void setSType(SType type) {
    this.type = type;
  }

  @Override
  public Location location() {
    return location;
  }
}
