package org.smoothbuild.lang.base.type.api;

import com.google.common.collect.ImmutableSet;

/**
 * This class and all its subclasses are immutable.
 */
public interface Type {

  public String name();

  public ImmutableSet<? extends Variable> variables();

  public default String q() {
    return "`" + name() + "`";
  }

  /**
   * @return true iff this type contains type variable(s).
   */
  public default boolean isPolytype() {
    return !variables().isEmpty();
  }
}
