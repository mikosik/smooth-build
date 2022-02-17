package org.smoothbuild.lang.type.api;

import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public sealed interface Type extends Named
    permits TypeBBridge, AbstractT, BaseT, ComposedT, VarT {

  @Override
  public String name();

  /**
   * @return true iff this type contains type var(s).
   */
  public default boolean isPolytype() {
    return hasOpenVars() || hasClosedVars();
  }

  public default boolean hasOpenVars() {
    return !openVars().isEmpty();
  }

  public boolean hasClosedVars();

  public ImmutableSet<? extends OpenVarT> openVars();
}
