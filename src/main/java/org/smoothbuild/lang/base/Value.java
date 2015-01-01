package org.smoothbuild.lang.base;

import com.google.common.hash.HashCode;

/**
 * A value in smooth language.
 */
public interface Value {
  public Type type();

  public HashCode hash();
}
