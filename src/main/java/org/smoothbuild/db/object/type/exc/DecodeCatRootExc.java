package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatKindH;

public class DecodeCatRootExc extends DecodeCatExc {
  public DecodeCatRootExc(Hash hash, int actualSize) {
    super(("Cannot decode category at %s. Its root points to hash sequence with %d elems when it "
        + "should point to sequence with 1 or 2 elems.")
        .formatted(hash, actualSize));
  }

  public DecodeCatRootExc(Hash hash, CatKindH kind, int size, int expectedSize) {
    super("Cannot decode %s category at %s. Its merkle root has %d children when %d is expected."
        .formatted(kind.name(), hash, size, expectedSize));
  }
}
