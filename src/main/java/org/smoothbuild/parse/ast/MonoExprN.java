package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Loc;

public sealed abstract class MonoExprN extends MonoAstNode implements ExprN
    permits CallN, OrderN, RefN, SelectN {
  public MonoExprN(Loc loc) {
    super(loc);
  }
}
