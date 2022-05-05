package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.AnyT;

/**
 * This class is immutable.
 */
public final class AnyTS extends BaseTS implements AnyT {
  public AnyTS() {
    super(TypeNamesS.ANY);
  }
}
