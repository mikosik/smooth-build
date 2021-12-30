package org.smoothbuild.bytecode.obj.exc;

import org.smoothbuild.bytecode.type.base.CatB;
import org.smoothbuild.db.hashed.Hash;

public class DecodeObjWrongSeqSizeExc extends DecodeObjNodeExc {
  public DecodeObjWrongSeqSizeExc(Hash hash, CatB cat, String path, int expectedSize,
      int actualSize) {
    super(hash, cat, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
