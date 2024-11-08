package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.Located;
import org.smoothbuild.common.log.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.SType;

/**
 * Expression in smooth language.
 */
public abstract sealed class PExpr implements Located
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

  public SType typeS() {
    return type;
  }

  public void setTypeS(SType type) {
    this.type = type;
  }
}
