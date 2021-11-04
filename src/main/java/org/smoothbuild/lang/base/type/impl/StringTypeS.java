package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTypeS extends BaseTypeS implements StringType {
  public StringTypeS() {
    super(TypeNames.STRING);
  }
}
