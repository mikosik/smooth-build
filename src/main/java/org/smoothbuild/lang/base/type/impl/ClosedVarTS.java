package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.ClosedVarT;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class ClosedVarTS extends VarTS implements ClosedVarT {
  public ClosedVarTS(String name) {
    super(name, ImmutableSet.of(), true);
  }
}
