package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.define.Loc;

public abstract sealed class ExprN extends ObjN
    permits CallN, OrderN, RefN, SelectN {
  public ExprN(Loc loc) {
    super(loc);
  }
}
