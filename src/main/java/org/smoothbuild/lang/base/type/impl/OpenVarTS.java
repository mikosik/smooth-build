package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.OpenVarT;

/**
 * This class is immutable.
 */
public final class OpenVarTS extends VarTS implements OpenVarT {
  public OpenVarTS(String name) {
    super(name, true, false);
  }
}
