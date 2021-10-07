package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.StringType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class StringTypeImpl extends BaseTypeImpl implements StringType {
  public StringTypeImpl() {
    super(TypeNames.STRING);
  }
}
