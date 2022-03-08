package org.smoothbuild.lang.type.api;

import org.smoothbuild.util.collect.Named;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface Type extends Named
    permits TypeBBridge, AbstractT, BaseT, ComposedT, Var {

  @Override
  public String name();

  /**
   * @return true iff this type contains type var(s).
   */
  public default boolean hasVars() {
    return !vars().isEmpty();
  }

  public VarSet<?> vars();
}
