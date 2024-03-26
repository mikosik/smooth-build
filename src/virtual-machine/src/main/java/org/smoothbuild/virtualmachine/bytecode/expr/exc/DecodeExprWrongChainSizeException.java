package org.smoothbuild.virtualmachine.bytecode.expr.exc;

import org.smoothbuild.common.base.Hash;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BKind;

public class DecodeExprWrongChainSizeException extends DecodeExprNodeException {
  public DecodeExprWrongChainSizeException(
      Hash hash, BKind kind, String path, int expectedSize, int actualSize) {
    super(
        hash,
        kind,
        path,
        "Node is a chain with wrong size. Expected " + expectedSize + " but was " + actualSize
            + ".");
  }
}
