package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.HasLocationImpl;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression in smooth language.
 */
public abstract sealed class PExpr extends HasLocationImpl
    permits PCall, PInstantiate, PLiteral, PNamedArg, POrder, PSelect {
  private SType type;

  public PExpr(Location location) {
    super(location);
  }

  public SType sType() {
    return type;
  }

  public void setSType(SType type) {
    this.type = type;
  }
}
