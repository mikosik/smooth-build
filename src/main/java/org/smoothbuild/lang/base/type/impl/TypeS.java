package org.smoothbuild.lang.base.type.impl;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.AbstractType;

import com.google.common.collect.ImmutableSet;

/**
 * Smooth language type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class TypeS extends AbstractType
    permits ArrayTypeS, BaseTypeS, FuncTypeS, StructTypeS, VarS {
  protected TypeS(String name, ImmutableSet<VarS> vars) {
    super(name, vars);
  }

  @Override
  public ImmutableSet<VarS> vars() {
    return (ImmutableSet<VarS>) super.vars();
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
