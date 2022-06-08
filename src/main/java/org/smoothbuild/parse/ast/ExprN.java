package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public sealed abstract class ExprN extends MonoAstNode implements ObjN
    permits CallN, OrderN, RefN, SelectN {
  public ExprN(Loc loc) {
    super(loc);
  }
}
