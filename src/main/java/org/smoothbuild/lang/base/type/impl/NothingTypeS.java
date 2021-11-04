package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingTypeS extends BaseTypeS implements NothingType {
  public NothingTypeS() {
    super(TypeNames.NOTHING);
  }
}
