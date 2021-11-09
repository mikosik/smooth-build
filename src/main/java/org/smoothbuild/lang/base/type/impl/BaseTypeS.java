package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

/**
 * This class is immutable.
 */
public class BaseTypeS extends TypeS {
  public BaseTypeS(String name) {
    super(name, set());
  }
}

