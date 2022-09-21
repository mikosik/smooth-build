package org.smoothbuild.bytecode.expr.exc;

import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.CategoryB;

public class DecodeExprWrongSeqSizeExc extends DecodeExprNodeExc {
  public DecodeExprWrongSeqSizeExc(Hash hash, CategoryB cat, String path, int expectedSize,
      int actualSize) {
    super(hash, cat, path, "Node is a sequence with wrong size. Expected " + expectedSize
        + " but was " + actualSize + ".");
  }
}
