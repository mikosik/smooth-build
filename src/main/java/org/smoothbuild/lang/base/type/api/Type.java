package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface Type extends Named
    permits AbstractT, ArrayT, BaseT, FuncT, Var {

  @Override
  public String name();

  public ImmutableSet<? extends Var> vars();

  /**
   * @return true iff this type contains type var(s).
   */
  public default boolean isPolytype() {
    return !vars().isEmpty();
  }
}
