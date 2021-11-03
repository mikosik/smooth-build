package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.IntType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class IntSType extends BaseSType implements IntType {
  public IntSType() {
    super(TypeNames.INT);
  }
}
