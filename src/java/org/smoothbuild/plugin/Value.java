package org.smoothbuild.plugin;

import org.smoothbuild.function.base.Type;

/**
 * A value in smooth language.
 */
public interface Value extends Hashed {
  public Type type();
}
