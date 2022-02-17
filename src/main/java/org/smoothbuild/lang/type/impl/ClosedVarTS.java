package org.smoothbuild.lang.type.impl;

import org.smoothbuild.lang.type.api.ClosedVarT;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class ClosedVarTS extends VarTS implements ClosedVarT {
  public ClosedVarTS(String name) {
    super(name, ImmutableSet.of(), true);
  }
}
