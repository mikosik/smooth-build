package org.smoothbuild.virtualmachine.bytecode.type.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.KindId;

public class DecodeKindRootException extends DecodeKindException {
  public DecodeKindRootException(Hash hash, int actualSize) {
    super(("Cannot decode kind at %s. Its root points to hash sequence with %d elems when it "
            + "should point to sequence with 1 or 2 elems.")
        .formatted(hash, actualSize));
  }

  public DecodeKindRootException(Hash hash, KindId kindId, int size, int expectedSize) {
    super("Cannot decode %s kind at %s. Its merkle root has %d children when %d is expected."
        .formatted(kindId, hash, size, expectedSize));
  }
}
