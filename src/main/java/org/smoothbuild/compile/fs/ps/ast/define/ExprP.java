package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Located;
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public sealed abstract class ExprP implements Located
    permits CallP, LiteralP, MonoizeP, NamedArgP, OrderP, SelectP {
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
