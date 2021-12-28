package org.smoothbuild.db.bytecode.type.exc;

import org.smoothbuild.db.bytecode.type.base.CatKindB;
import org.smoothbuild.db.hashed.Hash;

public class DecodeCatWrongSeqSizeExc extends DecodeCatNodeExc {
  public DecodeCatWrongSeqSizeExc(Hash hash, CatKindB kind, String path, int expectedSize,
      int actualSize) {
    super(hash, kind, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
