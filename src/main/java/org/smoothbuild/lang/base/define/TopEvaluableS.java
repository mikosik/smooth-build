package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

public sealed abstract class TopEvaluableS extends EvaluableImplS implements EvaluableS
    permits FunctionS, ValueS {
  public TopEvaluableS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }
}
