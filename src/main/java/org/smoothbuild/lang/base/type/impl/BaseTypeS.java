package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.base.type.api.BaseType;

/**
 * This class is immutable.
 */
public class BaseTypeS extends TypeS implements BaseType {
  public BaseTypeS(String name) {
    super(name, set());
  }
}
