package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.compilerfrontend.lang.base.location.Located;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public abstract sealed class ExprP implements Located
    permits CallP, InstantiateP, LiteralP, NamedArgP, OrderP, SelectP {
  private TypeS type;
  private final Location location;

  public ExprP(Location location) {
    this.location = location;
  }

  @Override
  public Location location() {
    return location;
  }

  public TypeS typeS() {
    return type;
  }

  public void setTypeS(TypeS type) {
    this.type = type;
  }
}
