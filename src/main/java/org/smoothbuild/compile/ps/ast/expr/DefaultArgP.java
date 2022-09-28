package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.PolyEvaluableS;

public final class DefaultArgP extends MonoizableP {
  private final PolyEvaluableS polyEvaluable;

  public DefaultArgP(PolyEvaluableS polyEvaluable, Loc loc) {
    super(loc);
    this.polyEvaluable = polyEvaluable;
  }

  public PolyEvaluableS polyEvaluableS() {
    return polyEvaluable;
  }
}
