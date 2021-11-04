package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BoolTypeS extends BaseTypeS implements BoolType {
  public BoolTypeS() {
    super(TypeNames.BOOL);
  }
}
