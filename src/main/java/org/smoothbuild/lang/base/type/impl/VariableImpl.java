package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.Sets.set;

import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * Type variable.
 *
 * This class is immutable.
 */
public class VariableImpl extends AbstractTypeImpl implements Variable {
  private final ImmutableSet<Variable> variables;

  public VariableImpl(String name) {
    super(name, null);
    this.variables = set(this);
  }

  @Override
  public ImmutableSet<Variable> variables() {
    return variables;
  }
}
