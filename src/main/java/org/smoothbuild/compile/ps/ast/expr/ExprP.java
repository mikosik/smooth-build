package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.WithLocImpl;
import org.smoothbuild.compile.lang.type.TypeS;

/**
 * Expression in smooth language.
 */
public sealed abstract class ExprP extends WithLocImpl
    permits BlobP, CallP, IntP, MonoizableP, NamedArgP, OrderP, SelectP, StringP {
  private TypeS type;

  public ExprP(Loc loc) {
    super(loc);
  }

  public TypeS typeS() {
    return type;
  }

  public void setTypeS(TypeS type) {
    this.type = type;
  }
}
