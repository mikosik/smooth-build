package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTypeImpl extends BaseTypeImpl implements IntType {
  public IntTypeImpl() {
    super(TypeNames.INT);
  }
}
