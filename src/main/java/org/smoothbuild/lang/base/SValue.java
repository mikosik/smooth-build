package org.smoothbuild.lang.base;

/**
 * Smooth Value. A value in smooth language.
 */
public interface SValue extends Hashed {
  public SType<? extends SValue> type();
}
