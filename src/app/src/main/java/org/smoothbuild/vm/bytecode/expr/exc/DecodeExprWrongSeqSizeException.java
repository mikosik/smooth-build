package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;

public class DecodeExprWrongSeqSizeException extends DecodeExprNodeException {
  public DecodeExprWrongSeqSizeException(
      Hash hash, CategoryB cat, String path, int expectedSize, int actualSize) {
    super(
        hash,
        cat,
        path,
        "Node is a sequence with wrong size. Expected " + expectedSize + " but was " + actualSize
            + ".");
  }
}
