package org.smoothbuild.vm.bytecode.type.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryKindB;

public class DecodeCatRootExc extends DecodeCatExc {
  public DecodeCatRootExc(Hash hash, int actualSize) {
    super(("Cannot decode category at %s. Its root points to hash sequence with %d elems when it "
        + "should point to sequence with 1 or 2 elems.")
        .formatted(hash, actualSize));
  }

  public DecodeCatRootExc(Hash hash, CategoryKindB kind, int size, int expectedSize) {
    super("Cannot decode %s category at %s. Its merkle root has %d children when %d is expected."
        .formatted(kind.name(), hash, size, expectedSize));
  }
}
