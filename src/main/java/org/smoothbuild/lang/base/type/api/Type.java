package org.smoothbuild.lang.base.type.api;

import org.smoothbuild.util.collect.Named;

import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public interface Type extends Named {

  @Override
  public String name();

  public ImmutableSet<? extends Variable> variables();

  /**
   * @return true iff this type contains type variable(s).
   */
  public default boolean isPolytype() {
    return !variables().isEmpty();
  }
}
