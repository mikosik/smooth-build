package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.VarT;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public abstract sealed class VarTS extends TypeS implements VarT
    permits ClosedVarTS, OpenVarTS {
  protected VarTS(String name, ImmutableSet<OpenVarTS> openVars, boolean hasClosedVars) {
    super(name, openVars, hasClosedVars);
  }
}