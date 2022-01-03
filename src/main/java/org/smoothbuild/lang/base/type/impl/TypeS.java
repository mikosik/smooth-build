package org.smoothbuild.lang.base.type.impl;

import java.util.Objects;

import org.smoothbuild.lang.base.type.api.AbstractT;

import com.google.common.collect.ImmutableSet;

/**
 * Smooth language type.
 * This class and all its subclasses are immutable.
 */
public abstract sealed class TypeS extends AbstractT
    permits ArrayTS, BaseTS, FuncTS, StructTS, VarTS {
  protected TypeS(String name, ImmutableSet<VarTS> vars) {
    super(name, vars);
  }

  @Override
  public ImmutableSet<VarTS> vars() {
    return (ImmutableSet<VarTS>) super.vars();
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

  @Override
  public String toString() {
    return "Type(`" + name() + "`)";
  }
}
