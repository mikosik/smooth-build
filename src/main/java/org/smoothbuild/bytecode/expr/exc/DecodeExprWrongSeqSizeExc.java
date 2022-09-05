package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.type.CatB;
import org.smoothbuild.db.Hash;

public class DecodeExprWrongSeqSizeExc extends DecodeExprNodeExc {
  public DecodeExprWrongSeqSizeExc(Hash hash, CatB cat, String path, int expectedSize,
      int actualSize) {
    super(hash, cat, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
