package org.smoothbuild.lang.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.type.api.VarT;

/**
 * This class is immutable.
 */
public final class VarTS extends TypeS implements VarT    {
  private final VarSetS vars;

  public VarTS(String name) {
    super(name, null);
    this.vars = new VarSetS(set(this));
  }

  @Override
  public VarSetS vars() {
    return vars;
  }
}
