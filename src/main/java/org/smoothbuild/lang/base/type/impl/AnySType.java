package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.AnyType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class AnySType extends BaseSType implements AnyType {
  public AnySType() {
    super(TypeNames.ANY);
  }
}
