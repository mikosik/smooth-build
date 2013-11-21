package org.smoothbuild.lang.type;

import org.smoothbuild.lang.function.base.Type;

/**
 * A value in smooth language.
 */
public interface Value extends Hashed {
  public Type type();
}
