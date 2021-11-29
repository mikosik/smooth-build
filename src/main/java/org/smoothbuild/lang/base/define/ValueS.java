package org.smoothbuild.lang.base.define;

import org.smoothbuild.lang.base.type.impl.TypeS;

/**
 * This class is immutable.
 */
public sealed abstract class ValueS extends TopEvalS permits BoolValueS, DefinedValueS {
  public ValueS(TypeS type, ModulePath modulePath, String name, Location location) {
    super(type, modulePath, name, location);
  }
}


