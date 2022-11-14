package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.NamedPolyEvaluableS;

public final class DefaultArgP extends MonoizableP {
  private final NamedPolyEvaluableS polyEvaluable;

  public DefaultArgP(NamedPolyEvaluableS polyEvaluable, Loc loc) {
    super(loc);
    this.polyEvaluable = polyEvaluable;
  }

  public NamedPolyEvaluableS polyEvaluableS() {
    return polyEvaluable;
  }
}
