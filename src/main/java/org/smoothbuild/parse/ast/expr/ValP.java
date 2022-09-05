package org.smoothbuild.parse.ast.expr;

import org.smoothbuild.lang.base.Loc;
import org.smoothbuild.lang.base.Tal;
import org.smoothbuild.lang.type.TypeS;

public sealed abstract class ValP extends Tal implements ExprP
    permits BlobP, IntP, StringP {
  public ValP(TypeS type, Loc loc) {
    super(type, loc);
  }
}
