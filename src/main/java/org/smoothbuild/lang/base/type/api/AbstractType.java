package org.smoothbuild.lang.base.type.api;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableSet;

public abstract class AbstractType implements Type {
  protected final String name;
  protected final ImmutableSet<? extends Variable> variables;

  public AbstractType(String name, ImmutableSet<? extends Variable> variables) {
    checkArgument(!name.isBlank());
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
  public ImmutableSet<? extends Variable> variables() {
    return variables;
  }

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}
