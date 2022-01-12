package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.util.collect.Named;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface Type extends Named
    permits AbstractT, BaseT, ComposedT, VarT {

  @Override
  public String name();

  /**
   * @return true iff this type contains type var(s).
   */
  public default boolean isPolytype() {
    return hasOpenVars() || hasClosedVars();
  }

  public boolean hasOpenVars();

  public boolean hasClosedVars();
}
