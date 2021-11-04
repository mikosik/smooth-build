package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.AnyType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class AnyTypeS extends BaseTypeS implements AnyType {
  public AnyTypeS() {
    super(TypeNames.ANY);
  }
}