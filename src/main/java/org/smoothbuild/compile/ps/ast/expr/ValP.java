package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.base.Tal;
import org.smoothbuild.compile.lang.type.TypeS;

public sealed abstract class ValP extends Tal implements ExprP
    permits BlobP, IntP, StringP {
  public ValP(TypeS type, Loc loc) {
    super(type, loc);
  }
}
