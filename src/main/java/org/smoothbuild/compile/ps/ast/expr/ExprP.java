package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.location.Located;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public sealed abstract class ExprP implements Located
    permits BlobP, CallP, IntP, MonoizableP, NamedArgP, OrderP, SelectP, StringP {
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
