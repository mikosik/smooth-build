package org.smoothbuild.db.object.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.spec.base.SpecKind;

public class DecodeSpecRootException extends DecodeSpecException {
  public DecodeSpecRootException(Hash hash, int actualSize) {
    super(("Cannot decode spec at %s. Its root points to hash sequence with %d elements when it "
        + "should point to sequence with 1 or 2 elements.")
        .formatted(hash, actualSize));
  }

  public DecodeSpecRootException(Hash hash, SpecKind specKind, int size, int expectedSize) {
    super("Cannot decode %s spec at %s. Its merkle root has %d children when %d is expected."
        .formatted(specKind.name(), hash, size, expectedSize));
  }
}
