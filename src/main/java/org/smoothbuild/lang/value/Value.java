package org.smoothbuild.lang.value;

import org.smoothbuild.lang.type.Type;

import com.google.common.hash.HashCode;

/**
 * A value in smooth language.
 */
public interface Value {
  public Type type();

  public HashCode hash();
}
