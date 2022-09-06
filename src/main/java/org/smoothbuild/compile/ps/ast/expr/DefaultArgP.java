package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.ExprS;

public final class DefaultArgP extends OperP {
  private final RefP refP;
  private final ExprS exprS;

  public DefaultArgP(RefP refP, ExprS exprS, Loc loc) {
    super(loc);
    this.refP = refP;
    this.exprS = exprS;
  }

  public RefP refP() {
    return refP;
  }

  public ExprS exprS() {
    return exprS;
  }
}