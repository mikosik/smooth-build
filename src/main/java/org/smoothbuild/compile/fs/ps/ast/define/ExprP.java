package org.smoothbuild.compile.fs.ps.ast.define;

import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public sealed abstract class ExprP
    extends TalP
    permits CallP, InstantiateP, LiteralP, NamedArgP, OrderP, SelectP {
  private TypeS type;

  public ExprP(Location location) {
    super(location);
  }

  public TypeS typeS() {
    return type;
  }

  public void setTypeS(TypeS type) {
    this.type = type;
  }
}
