package org.smoothbuild.lang.base.type.impl;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.AbstractType;

import com.google.common.collect.ImmutableSet;

/**
 * Smooth language type.
 * This class and all its subclasses are immutable.
 */
public abstract class TypeS extends AbstractType {
  protected TypeS(String name, ImmutableSet<VariableS> variables) {
    super(name, variables);
  }

  @Override
  public ImmutableSet<VariableS> variables() {
    return (ImmutableSet<VariableS>) super.variables();
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof TypeS that && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}
