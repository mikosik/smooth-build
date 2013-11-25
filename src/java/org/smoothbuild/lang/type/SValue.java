package org.smoothbuild.lang.type;

/**
 * Smooth Value. A value in smooth language.
 */
public interface SValue extends Hashed {
  public SType<? extends SValue> type();
}
