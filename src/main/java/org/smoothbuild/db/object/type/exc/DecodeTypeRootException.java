package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.TypeKindH;

public class DecodeTypeRootException extends DecodeTypeException {
  public DecodeTypeRootException(Hash hash, int actualSize) {
    super(("Cannot decode type at %s. Its root points to hash sequence with %d elements when it "
        + "should point to sequence with 1 or 2 elements.")
        .formatted(hash, actualSize));
  }

  public DecodeTypeRootException(Hash hash, TypeKindH kind, int size, int expectedSize) {
    super("Cannot decode %s type at %s. Its merkle root has %d children when %d is expected."
        .formatted(kind.name(), hash, size, expectedSize));
  }
}
