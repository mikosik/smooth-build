package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class VarS extends TypeS implements Var {
  private final ImmutableSet<VarS> vars;

  public VarS(String name) {
    super(name, null);
    this.vars = set(this);
  }

  @Override
  public ImmutableSet<VarS> vars() {
    return vars;
  }
}
