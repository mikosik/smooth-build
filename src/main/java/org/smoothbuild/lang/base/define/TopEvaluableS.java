package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

public abstract class TopEvaluableS extends EvaluableImplS implements EvaluableS {
  public TopEvaluableS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }
}
