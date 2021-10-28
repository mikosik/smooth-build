package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.expr.Expression;

public interface DefinedEvaluable extends Evaluable {
  public Expression body();
}
