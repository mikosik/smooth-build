package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.WithLoc;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public sealed abstract class ExprP implements WithLoc
    permits BlobP, CallP, IntP, MonoizableP, NamedArgP, OrderP, SelectP, StringP {
  private TypeS type;
  private final Loc loc;

  public ExprP(Loc loc) {
    this.loc = loc;
  }

  @Override
  public Loc loc() {
    return loc;
  }

  public TypeS typeS() {
    return type;
  }

  public void setTypeS(TypeS type) {
    this.type = type;
  }
}
