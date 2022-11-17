package org.smoothbuild.compile.ps.ast.expr;

import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.lang.define.NamedEvaluableS;

public final class DefaultArgP extends MonoizableP {
  private final NamedEvaluableS evaluable;

  public DefaultArgP(NamedEvaluableS evaluable, Loc loc) {
    super(loc);
    this.evaluable = evaluable;
  }

  public NamedEvaluableS evaluableS() {
    return evaluable;
  }
}
