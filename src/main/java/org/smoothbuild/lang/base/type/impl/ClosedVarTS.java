package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.ClosedVarT;

/**
 * This class is immutable.
 */
public final class ClosedVarTS extends VarTS implements ClosedVarT {
  public ClosedVarTS(String name) {
    super(name, false, true);
  }
}
