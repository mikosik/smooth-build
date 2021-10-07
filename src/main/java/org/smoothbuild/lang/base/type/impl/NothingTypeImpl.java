package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTypeImpl extends BaseTypeImpl implements NothingType {
  public NothingTypeImpl() {
    super(TypeNames.NOTHING);
  }
}
