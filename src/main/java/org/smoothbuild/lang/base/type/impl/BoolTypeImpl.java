package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BoolTypeImpl extends BaseTypeImpl implements BoolType {
  public BoolTypeImpl() {
    super(TypeNames.BOOL);
  }
}
