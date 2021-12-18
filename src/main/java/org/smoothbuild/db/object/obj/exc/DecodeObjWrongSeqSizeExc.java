package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatB;

public class DecodeObjWrongSeqSizeExc extends DecodeObjNodeExc {
  public DecodeObjWrongSeqSizeExc(Hash hash, CatB cat, String path, int expectedSize,
      int actualSize) {
    super(hash, cat, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
