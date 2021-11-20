package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public final class VariableS extends TypeS implements Variable {
  private final ImmutableSet<VariableS> variables;

  public VariableS(String name) {
    super(name, null);
    this.variables = set(this);
  }

  @Override
  public ImmutableSet<VariableS> variables() {
    return variables;
  }
}
