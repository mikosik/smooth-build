package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.type.BCategory;

public class DecodeExprWrongChainSizeException extends DecodeExprNodeException {
  public DecodeExprWrongChainSizeException(
      Hash hash, BCategory cat, String path, int expectedSize, int actualSize) {
    super(
        hash,
        cat,
        path,
        "Node is a chain with wrong size. Expected " + expectedSize + " but was " + actualSize
            + ".");
  }
}
