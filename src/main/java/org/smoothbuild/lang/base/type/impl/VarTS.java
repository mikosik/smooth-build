package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.VarT;

/**
 * This class is immutable.
 */
public abstract sealed class VarTS extends TypeS implements VarT
    permits ClosedVarTS, OpenVarTS {
  protected VarTS(String name, boolean hasOpenVars, boolean hasClosedVars) {
    super(name, hasOpenVars, hasClosedVars);
  }
}
