package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.AnyT;
import org.smoothbuild.lang.type.api.TypeNames;

/**
 * This class is immutable.
 */
public final class AnyTS extends BaseTS implements AnyT {
  public AnyTS() {
    super(TypeNames.ANY);
  }
}
