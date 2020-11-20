package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.internal;

/**
 * This class is immutable.
 */
public abstract class BaseType extends Type {
  public BaseType(String name) {
    super(name, internal(), false);
  }
}

