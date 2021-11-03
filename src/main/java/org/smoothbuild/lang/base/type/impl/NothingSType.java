package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.NothingType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class NothingSType extends BaseSType implements NothingType {
  public NothingSType() {
    super(TypeNames.NOTHING);
  }
}
