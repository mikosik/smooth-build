package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractType implements Type {
  protected final String name;
  protected final ImmutableSet<Variable> variables;

  public AbstractType(String name, ImmutableSet<Variable> variables) {
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
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}
