package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.expr.ExprS;

public sealed interface DefinedEvaluableS extends EvaluableS
    permits DefinedFunctionS, DefinedValueS {
  public ExprS body();
}
