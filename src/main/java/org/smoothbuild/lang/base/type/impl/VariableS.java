package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * This class is immutable.
 */
public class VariableS extends TypeS implements Variable {
  private final ImmutableSet<Variable> variables;

  public VariableS(String name) {
    super(name, null);
    this.variables = set(this);
  }

  @Override
  public ImmutableSet<Variable> variables() {
    return variables;
  }
}
