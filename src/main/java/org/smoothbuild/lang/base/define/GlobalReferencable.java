package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

public abstract class GlobalReferencable extends Referencable implements EvaluableS {
  public GlobalReferencable(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }
}
