package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobTypeImpl extends BaseTypeImpl implements BlobType {
  public BlobTypeImpl() {
    super(TypeNames.BLOB);
  }
}
