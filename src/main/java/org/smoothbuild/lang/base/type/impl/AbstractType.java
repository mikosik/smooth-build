package org.smoothbuild.lang.base.type.impl;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.Type;
import org.smoothbuild.lang.base.type.api.Variable;

import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public abstract class AbstractType implements Type {
  private final String name;
  private final ImmutableSet<Variable> variables;

  protected AbstractType(String name, ImmutableSet<Variable> variables) {
    this.name = name;
    this.variables = variables;
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * @return type variables sorted alphabetically
   */
  @Override
  public ImmutableSet<Variable> variables() {
      return variables;
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

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}
