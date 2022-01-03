package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.base.type.api.Var;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class VarTS extends TypeS implements Var {
  private final ImmutableSet<VarTS> vars;

  public VarTS(String name) {
    super(name, null);
    this.vars = set(this);
  }

  @Override
  public ImmutableSet<VarTS> vars() {
    return vars;
  }
}
