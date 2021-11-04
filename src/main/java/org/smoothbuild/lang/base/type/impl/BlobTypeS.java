package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobTypeS extends BaseTypeS implements BlobType {
  public BlobTypeS() {
    super(TypeNames.BLOB);
  }
}
