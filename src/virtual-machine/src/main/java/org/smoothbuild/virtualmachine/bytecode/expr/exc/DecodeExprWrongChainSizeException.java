package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.CategoryB;

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
