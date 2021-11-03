package org.smoothbuild.lang.base.type.impl;

import org.smoothbuild.lang.base.type.api.BlobType;
import org.smoothbuild.lang.base.type.api.TypeNames;

/**
 * This class is immutable.
 */
public class BlobSType extends BaseSType implements BlobType {
  public BlobSType() {
    super(TypeNames.BLOB);
  }
}
