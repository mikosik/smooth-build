package org.smoothbuild.db.object.type.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatKindH;

public class DecodeCatWrongSeqSizeExc extends DecodeCatNodeExc {
  public DecodeCatWrongSeqSizeExc(Hash hash, CatKindH kind, String path, int expectedSize,
      int actualSize) {
    super(hash, kind, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
