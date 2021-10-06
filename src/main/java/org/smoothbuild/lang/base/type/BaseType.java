package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.Sets.set;

/**
 * This class is immutable.
 */
public abstract class BaseType extends Type {
  public BaseType(String name) {
    super(name, set());
  }
}

