package org.smoothbuild.lang.type.api;

import org.smoothbuild.util.collect.Named;

/**
 * This class and all its subclasses are immutable.
 */
public interface Type extends Named {

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
