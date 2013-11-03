package org.smoothbuild.plugin;

import org.smoothbuild.function.base.Type;

import com.google.common.hash.HashCode;

/**
 * A value in smooth language.
 */
public interface Value {
  public Type type();

  public HashCode hash();
}
