package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.expr.Annotation;

public sealed interface NativeEvaluableS extends EvaluableS permits NativeFunctionS, NativeValueS {
  public Annotation annotation();
}
