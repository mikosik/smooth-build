package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntTypeS extends BaseTypeS implements IntType {
  public IntTypeS() {
    super(TypeNames.INT);
  }
}
