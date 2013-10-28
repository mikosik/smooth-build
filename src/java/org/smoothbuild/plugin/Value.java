package org.smoothbuild.plugin;

import com.google.common.hash.HashCode;

/**
 * A value in smooth language.
 */
public interface Value {
  public HashCode hash();
}
