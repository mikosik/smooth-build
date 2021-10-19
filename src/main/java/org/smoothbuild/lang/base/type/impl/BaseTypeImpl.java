package org.smoothbuild.lang.base.type.impl;

import static org.smoothbuild.util.Sets.set;

import org.smoothbuild.lang.base.type.api.BaseType;

/**
 * This class is immutable.
 */
public class BaseTypeImpl extends AbstractTypeImpl implements BaseType {
  public BaseTypeImpl(String name) {
    super(name, set());
  }
}

