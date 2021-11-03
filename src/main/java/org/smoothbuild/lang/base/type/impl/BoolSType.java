package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.BoolType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BoolSType extends BaseSType implements BoolType {
  public BoolSType() {
    super(TypeNames.BOOL);
  }
}
