package org.smoothbuild.lang.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

/**
 * This class is immutable.
 */
public final class VarS extends TypeS {
  private final VarSetS vars;

  public VarS(String name) {
    super(name, null);
    this.vars = new VarSetS(set(this));
  }

  @Override
  public VarSetS vars() {
    return vars;
  }
}
