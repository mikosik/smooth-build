package org.smoothbuild.lang.base.type.api;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.ImmutableSet;

public non-sealed abstract class AbstractT implements Type {
  protected final String name;
  protected final ImmutableSet<? extends VarT> vars;

  public AbstractT(String name, ImmutableSet<? extends VarT> vars) {
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
  public ImmutableSet<? extends VarT> vars() {
    return vars;
  }
}
