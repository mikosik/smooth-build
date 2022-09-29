package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.WithLocImpl;
import org.smoothbuild.compile.lang.type.TypeS;

public sealed abstract class ValP extends WithLocImpl implements ExprP
    permits BlobP, IntP, StringP {
  private final TypeS typeS;

  public ValP(TypeS typeS, Loc loc) {
    super(loc);
    this.typeS = typeS;
  }

  public TypeS typeS() {
    return typeS;
  }
}
