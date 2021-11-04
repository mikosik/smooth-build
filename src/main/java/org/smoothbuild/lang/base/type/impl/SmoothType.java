package org.smoothbuild.lang.base.type.impl;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.AbstractType;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * Smooth language type.
 * This class and all its subclasses are immutable.
 */
public abstract class SmoothType extends AbstractType {
  protected SmoothType(String name, ImmutableSet<Variable> variables) {
    super(name, variables);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof AbstractType that
        && this.name().equals(that.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(name());
  }
}
