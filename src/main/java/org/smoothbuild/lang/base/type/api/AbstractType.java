package org.smoothbuild.lang.base.type.api;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableSet;

public non-sealed abstract class AbstractType implements Type {
  protected final String name;
  protected final ImmutableSet<? extends Var> vars;

  public AbstractType(String name, ImmutableSet<? extends Var> vars) {
    checkArgument(!name.isBlank());
    this.name = name;
    this.vars = vars;
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * @return type vars sorted alphabetically
   */
  @Override
  public ImmutableSet<? extends Var> vars() {
    return vars;
  }

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}
