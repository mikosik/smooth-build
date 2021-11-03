package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringSType extends BaseSType implements StringType {
  public StringSType() {
    super(TypeNames.STRING);
  }
}
