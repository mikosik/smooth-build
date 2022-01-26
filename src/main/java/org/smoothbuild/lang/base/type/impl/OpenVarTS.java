package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.OpenVarT;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class OpenVarTS extends VarTS implements OpenVarT {
  private final ImmutableSet<OpenVarTS> openVars;

  public OpenVarTS(String name) {
    super(name, null, false);
    this.openVars = ImmutableSet.of(this);
  }

  @Override
  public ImmutableSet<OpenVarTS> openVars() {
    return openVars;
  }
}
