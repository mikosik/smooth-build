package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatKindH;

public class DecodeTypeRootExc extends DecodeTypeExc {
  public DecodeTypeRootExc(Hash hash, int actualSize) {
    super(("Cannot decode type at %s. Its root points to hash sequence with %d elems when it "
        + "should point to sequence with 1 or 2 elems.")
        .formatted(hash, actualSize));
  }

  public DecodeTypeRootExc(Hash hash, CatKindH kind, int size, int expectedSize) {
    super("Cannot decode %s type at %s. Its merkle root has %d children when %d is expected."
        .formatted(kind.name(), hash, size, expectedSize));
  }
}
