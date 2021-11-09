package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.expr.ExprS;

public interface DefinedEvaluable extends Evaluable {
  public ExprS body();
}
