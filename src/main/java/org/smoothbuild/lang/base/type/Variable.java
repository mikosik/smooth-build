package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.Sets.set;

import org.smoothbuild.lang.base.type.Sides.Side;

import com.google.common.collect.ImmutableSet;

/**
 * Type variable.
 *
 * This class is immutable.
 */
public class Variable extends Type {
  private final ImmutableSet<Variable> variables;

  public Variable(String name) {
    super(name, new TypeConstructor(name), null);
    this.variables = set(this);
  }

  @Override
  public ImmutableSet<Variable> variables() {
    return variables;
  }

  @Override
  Type mapVariables(BoundsMap boundsMap, Side side, TypeFactory typeFactory) {
    return boundsMap.map().get(this).bounds().get(side);
  }
}
