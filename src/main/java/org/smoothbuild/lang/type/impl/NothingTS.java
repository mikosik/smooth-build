package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.NothingT;

/**
 * This class is immutable.
 */
public final class NothingTS extends BaseTS implements NothingT {
  public NothingTS() {
    super(TypeNamesS.NOTHING);
  }
}
