package org.smoothbuild.db.object.obj.exc;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.type.base.CatH;

public class UnexpectedObjSeqExc extends DecodeObjNodeExc {
  public UnexpectedObjSeqExc(Hash hash, CatH cat, String path, int expectedSize,
      int actualSize) {
    super(hash, cat, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
