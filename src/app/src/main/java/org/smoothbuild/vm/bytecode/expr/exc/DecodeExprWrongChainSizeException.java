package org.smoothbuild.vm.bytecode.expr.exc;

import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.CategoryB;

public class DecodeExprWrongChainSizeException extends DecodeExprNodeException {
  public DecodeExprWrongChainSizeException(
      Hash hash, CategoryB cat, String path, int expectedSize, int actualSize) {
    super(
        hash,
        cat,
        path,
        "Node is a chain with wrong size. Expected " + expectedSize + " but was " + actualSize
            + ".");
  }
}
