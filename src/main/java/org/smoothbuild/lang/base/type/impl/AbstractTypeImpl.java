package org.smoothbuild.lang.base.type.impl;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.AbstractType;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class AbstractTypeImpl extends AbstractType {
  protected AbstractTypeImpl(String name, ImmutableSet<Variable> variables) {
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
