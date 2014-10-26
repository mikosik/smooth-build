package org.smoothbuild.lang.base;

/**
 * A value in smooth language.
 */
public interface Value extends Hashed {
  public SType<? extends Value> type();
}
